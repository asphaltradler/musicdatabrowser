import {Component, OnDestroy} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {EntityService} from '../services/entity.service';
import {Subscription} from 'rxjs';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {Page} from '../entities/page';
import {appDefaults} from '../../config/config';
import {NgComponentOutlet} from '@angular/common';
import {Album} from '../entities/album';
import {Track} from '../entities/track';
import {ListHeaderComponent} from './list-header/list-header.component';
import {PagingComponent} from '../controls/paging.component';
import {TrackComponent} from './entity-component/track.component';
import {AlbumComponent} from './entity-component/album.component';
import {EntityComponent} from './entity-component/entity.component';
import {TableHeaderComponent} from './table-header/table-header.component';
import {TrackTableHeaderComponent} from './table-header/track-table-header.component';
import {AlbumTableHeaderComponent} from './table-header/album-table-header.component';
import {Title} from '@angular/platform-browser';
import {
  allEntities,
  getEntityForName,
  paramEntity,
  paramSearchEntity,
  paramSearchId,
  paramSearchString
} from '../../config/utilities';

@Component({
  templateUrl: './entity-list.component.html',
  styleUrls: ['./entity-list.component.css'],
  standalone: true,
  imports: [
    NgComponentOutlet,
    SearchfieldComponent,
    ListHeaderComponent,
    PagingComponent,
  ]
})
export class EntityListComponent<E extends AbstractEntity> implements OnDestroy {
  public page?: Page<E>;
  private _pageSize = appDefaults.defaultPageSize;

  private _filter = '';
  private titleFor = '';

  private changeSubscription: Subscription;
  private lastSearchSubscription?: Subscription;

  public lastSearchPerformance?: string;

  public entityType!: typeof AbstractEntity;
  public searchEntityType!: typeof AbstractEntity;

  private _searchableEntities?: typeof AbstractEntity[];
  private lastSearchId?: Number;
  private _searchName = '';

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title,
              public service: EntityService) {
    //default/Vorbelegung bei Aktivierung oder Änderung der Query
    this.changeSubscription = route.params.subscribe(() => {
      this.startSearchFromQuery();
    });
  }

  ngOnDestroy(): void {
    console.log(`destroy ${this.entityType.getNameSingular()}List`);
    this.changeSubscription?.unsubscribe();
    this.lastSearchSubscription?.unsubscribe();
    this._filter = '';
  }

  startSearchFromQuery(): void {
    const snapshot = this.route.snapshot;
    const params = snapshot.params;
    //EntityTyp aus Router übergeben oder aus neuer Suche
    this.entityType = getEntityForName(params[paramEntity]) || snapshot.data[0];

    if (this.entityType) {
      this.titleService.setTitle(this.entityType.namePlural);

      //mit eigenem Typ selbst anfangen als Suchkriterium, falls keiner gegeben
      this.searchEntityType = getEntityForName(params[paramSearchEntity]) || this.entityType;
      //eigenen Typ ausschließen in Darstellung
      this._searchableEntities = allEntities.filter(
        entity => entity != this.entityType
      );
      console.log(`${this.entityType.getNameSingular()}List created`);
      const searchEntityName = params[paramSearchEntity];
      if (searchEntityName) {
        const searchEntity = getEntityForName(searchEntityName);
        if (searchEntity) {
          const id = params[paramSearchId];
          const searchString = this.router.getCurrentNavigation()?.extras?.state?.[paramSearchString];
          if (id) {
            this.titleService.setTitle(`${this.entityType.namePlural} für ${searchString || id}`);
            this.searchByEntityId(searchEntity, Number.parseInt(id), searchString || '');
          } else if (searchString) {
            this.titleService.setTitle(`${this.entityType.namePlural} für ${searchString}`);
            this.searchByEntityName(searchEntity, searchString);
          }
        }
      } else {
        //default: alle anzeigen
        this.searchByEntityName(this.entityType, '');
      }
    }
  }

  searchByEntityName(searchEntityType: typeof AbstractEntity, searchString: string) {
    this.searchByEntityIdOrName(searchEntityType, 0, undefined, searchString);
  }

  searchByEntityId(searchEntityType: typeof AbstractEntity, id: Number, searchString: string) {
    this.searchByEntityIdOrName(searchEntityType, 0, id, searchString);
  }

  private searchByEntityIdOrName(searchEntityType: typeof AbstractEntity, pageNumber: number, id?: Number, searchString?: string) {
    //falls noch eine Suche unterwegs ist: abbrechen
    this.lastSearchSubscription?.unsubscribe();
    console.log(this.getSearchMessage(searchEntityType, id, searchString));
    const obs = id
      ? this.service.findByOtherId<E>(this.entityType, searchEntityType, id.valueOf(), pageNumber, this._pageSize)
      : searchEntityType === this.entityType
        ? this.service.findNameLike<E>(this.entityType, searchString?.toLowerCase() || '', pageNumber, this._pageSize)
        : this.service.findByOtherNameLike<E>(this.entityType, searchEntityType, searchString?.toLowerCase() || '', pageNumber, this._pageSize);
    const time = performance.now();
    this.lastSearchSubscription = obs.subscribe(page => {
      this.titleFor = searchString ? `für ${searchEntityType.getNameSingular()}='${searchString}'` : 'insgesamt';
      this.fillData(page, searchEntityType, id, searchString);
      const timeString = (performance.now() - time).toFixed(2);
      this.lastSearchPerformance = this.getSearchMessage(searchEntityType, id, searchString) + ` dauerte ${timeString}ms`;
      console.log(this.lastSearchPerformance);
    });
  }

  private getSearchMessage = (searchEntityType: typeof AbstractEntity, id: Number | undefined, searchString: string | undefined) => {
    return `Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName + id ? 'id ' + id : ''}=${searchString || '*'}`;
  }

  private fillData(page: Page<E>, searchEntityType: typeof AbstractEntity, searchId?: Number, searchString?: string) {
    this.page = page;
    this.searchEntityType = searchEntityType;
    this.lastSearchId = searchId;
    this._searchName = searchString || '';
  }

  searchPreviousPage(): void {
    if (this.searchEntityType && this.hasPreviousPage()) {
      this.searchByEntityIdOrName(this.searchEntityType, this.page!.number - 1, this.lastSearchId, this._searchName);
    }
  }

  searchNextPage(): void {
    if (this.searchEntityType && this.hasNextPage()) {
      this.searchByEntityIdOrName(this.searchEntityType, this.page!.number + 1, this.lastSearchId, this._searchName);
    }
  }

  navigateOtherEntityByThis(entityType: typeof AbstractEntity, entity: AbstractEntity) {
    this.navigateOtherEntityBy(entityType, this.entityType, entity);
  }

  navigateOtherEntityByItself(entityType: typeof AbstractEntity, entity: AbstractEntity) {
    this.navigateOtherEntityBy(entityType, entityType, entity);
  }

  /**
   * Sucht mittels einer gegebenen Entity als Suchkriterium in einer anderen Entity-Liste.
   * Beim Öffnen der entsprechenden View (anderen EntityListComponent) wird dann über die
   * queryParams die entsprechende Suche ausgelöst.
   * @param entityType der Typ, zu dem navigiert wird
   * @param searchEntityType der Typ, anhand dem gesucht werden soll
   * @param entity eine Entity des searchEntityType, nach der gesucht wird (anhand id)
   */
  navigateOtherEntityBy(entityType: typeof AbstractEntity,
                        searchEntityType: typeof AbstractEntity, entity: AbstractEntity) {
    console.log(`Navigiere nach ${entityType.entityName}/${searchEntityType.entityName}/${entity.id}='${entity.name}'`);
    const params: Params = {};
    params[paramSearchString] = entity.name;
    this.router.navigate([entityType.entityName, searchEntityType.entityName, entity.id],
      { onSameUrlNavigation: 'reload', state: params} );
  }

  getTableHeaderComponent() {
    switch (this.entityType) {
      case Album: return AlbumTableHeaderComponent;
      case Track: return TrackTableHeaderComponent;
      default: return TableHeaderComponent;
    }
  }

  getTableRowComponent() {
    switch (this.entityType) {
      case Album: return AlbumComponent;
      case Track: return TrackComponent;
      default: return EntityComponent;
    }
  }

  get pageSize(): number {
    return this._pageSize;
  }

  set pageSize(value: number) {
    this._pageSize = value;
    this.searchByEntityName(this.searchEntityType, this.searchName);
  }

  get searchName(): string {
    return this._searchName;
  }

  set searchName(value: string) {
    this._searchName = value;
    this.searchByEntityName(this.searchEntityType, this.searchName);
  }

  get filter(): string {
    return this._filter;
  }

  set filter(value: string) {
    this._filter = value.toLowerCase();
  }

  get title() {
    if (!this.page || !this.entityType) {
      return '';
    }
    let title;
    const entityCount = this.getEntities().filter(e => this.isEntityShown(e)).length;
    if (this._filter && entityCount < this.page.numberOfElements) {
      title = `${entityCount} von ${this.entityType.getNumberDescription(this.page.totalElements)}`;
    } else {
      const entityStart = this.page.number * this.page.size;
      title = entityCount !== this.page.totalElements
        ? `${this.entityType.getNumbersDescription(entityStart+1, this.page.numberOfElements + entityStart)} von ${this.page.totalElements}`
        : this.entityType.getNumberDescription(this.page.totalElements);
    }
    return `${title} ${this.titleFor}`;
  }

  getEntities(): E[] {
    return this.page?.content || [];
  }

  isEntityFiltered(entity: AbstractEntity): boolean {
    return !!this._filter && !entity.name?.toLowerCase().includes(this._filter);
  }

  isEntityShown(entity: AbstractEntity): boolean {
    return !this.isEntityFiltered(entity);
  }

  hasPreviousPage() {
    return !this.page?.first;
  }

  hasNextPage() {
    return !this.page?.last;
  }

  get searchableEntities() {
    return this._searchableEntities;
  }
}
