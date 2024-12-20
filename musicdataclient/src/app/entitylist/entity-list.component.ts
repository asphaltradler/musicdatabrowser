import {AfterViewInit, Component, OnDestroy, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, NavigationStart, Params, Router} from '@angular/router';
import {EntityService} from '../services/entity.service';
import {Subscription} from 'rxjs';
import {Page} from '../entities/page';
import {appDefaults} from '../../config/config';
import {Album} from '../entities/album';
import {Track} from '../entities/track';
import {TrackComponent} from './entity-component/track.component';
import {AlbumComponent} from './entity-component/album.component';
import {EntityComponent} from './entity-component/entity.component';
import {TableHeaderComponent} from './table-header/table-header.component';
import {TrackTableHeaderComponent} from './table-header/track-table-header.component';
import {AlbumTableHeaderComponent} from './table-header/album-table-header.component';
import {Title} from '@angular/platform-browser';
import {
  allEntities,
  detailsPath,
  getEntityForName,
  paramEntity,
  paramEntityType,
  paramFilterText,
  paramId,
  paramPageNumber,
  paramPageSize,
  paramSearchEntity,
  paramSearchText,
  paramSourceEntity,
} from '../../config/utilities';
import {DetailsPopupComponent} from '../popup/details/details.popup.component';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';
import {NgComponentOutlet, NgForOf} from '@angular/common';

@Component({
  templateUrl: './entity-list.component.html',
  styleUrls: ['./entity-list.component.css'],
  standalone: true,
  imports: [
    DetailsPopupComponent,
    SearchfieldComponent,
    PagingComponent,
    ListHeaderComponent,
    NgComponentOutlet,
    NgForOf,
  ]
})
export class EntityListComponent<E extends AbstractEntity> implements OnDestroy, AfterViewInit {
  public page?: Page<E>;
  private _pageSize = appDefaults.defaultPageSize;

  private _filterText = '';
  private titleFor = '';

  private routeChangeSubscription: Subscription;
  private lastSearchSubscription?: Subscription;

  public lastSearchPerformance?: string;

  public entityType!: typeof AbstractEntity;
  public searchEntityType!: typeof AbstractEntity;

  private _searchableEntities?: typeof AbstractEntity[];
  private lastSearchId?: Number;
  private _searchText = '';

  private lastClickedEntity?: E;
  private restoredFlag = false;

  @ViewChild(DetailsPopupComponent) popup!: DetailsPopupComponent<E>;
  @ViewChild(SearchfieldComponent) searchFieldComponent!: SearchfieldComponent;
  @ViewChildren(EntityComponent) entityComponents!: QueryList<EntityComponent<E>>;

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title,
              public service: EntityService) {
    //default/Vorbelegung bei Aktivierung oder Änderung der Query
    this.routeChangeSubscription = route.params.subscribe(() => {
      this.startSearchFromQuery();
    });
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        //console.log(`${this.entityType.entityName} NavigationStart ${event}`);
        if (event.restoredState) {
          console.log(`${this.entityType.entityName} aus Browser-Back/Forward`, event.restoredState);
          this.restoredFlag = true;
        } else {
          this.restoredFlag = false;
        }
      }
    })
  }

  ngAfterViewInit(): void {
    //TODO geht wohl nicht wg. ngComponentOutlet
    this.entityComponents.changes.subscribe(changes => {
      console.log(`changes ${changes}`);
    });
  }

  openPopup(entity: E, event: MouseEvent) {
    this.popup.open(entity.name, entity.albumartName || '',
      //TODO Position einfacher bestimmbar?
      { x: event.pageX + 10, y: event.pageY-event.offsetY + 10});
  }

  onPopupClosed() {
    console.log('Popup wurde geschlossen');
  }

  ngOnDestroy(): void {
    console.log(`destroy ${this.entityType.getNameSingular()}List`);
    this.routeChangeSubscription?.unsubscribe();
    this.lastSearchSubscription?.unsubscribe();
  }

  startSearchFromQuery(): void {
    const snapshot = this.route.snapshot;
    const params = snapshot.params;
    const navigation = this.router.getCurrentNavigation();
    //state, bevor wir von dieser Seite weggegangen sind
    const previousState = navigation?.previousNavigation?.extras?.state;
    this.lastClickedEntity = previousState?.[paramSourceEntity];
    console.log(`lastClickedEntity=${this.lastClickedEntity?.name}`);
    //EntityTyp aus neuer Suche in params oder direkt aus aus Router (app-routes data) übergeben
    this.entityType = getEntityForName(params[paramEntityType]) || snapshot.data[0];

    if (this.entityType) {
      //eigenen Typ ausschließen in Darstellung
      this._searchableEntities = allEntities.filter(
        entity => entity != this.entityType
      );
      console.log(`${this.entityType.getNameSingular()}List created`);
      //SearchEntityType als Parameter übergeben
      const searchEntityType = getEntityForName(params[paramSearchEntity]
        || previousState?.[paramSearchEntity])  //oder aus vorhergehendem State
        || this.entityType;  //oder default: unser eigener (bei Start aus Router)
      const pageNumber = previousState?.[paramPageNumber];
      //nicht setter aufrufen, der macht selber Suche!
      this._pageSize = previousState?.[paramPageSize] || appDefaults.defaultPageSize;
      this.filterText = previousState?.[paramFilterText] || '';

      const entity: AbstractEntity = navigation?.extras?.state?.[paramEntity];
      const id = entity?.id;
      const name = entity?.name || previousState?.[paramSearchText];
      if (id) {
        this.searchByEntityId(searchEntityType, id, name || '', pageNumber);
      } else {
        this.searchByEntityName(searchEntityType, name, pageNumber);
      }
    }
  }

  searchByEntityName(searchEntityType: typeof AbstractEntity, searchString: string, pageNumber = 0) {
    this.searchByEntityIdOrName(searchEntityType, pageNumber, undefined, searchString);
  }

  searchByEntityId(searchEntityType: typeof AbstractEntity, id: Number, searchString: string, pageNumber = 0) {
    this.searchByEntityIdOrName(searchEntityType, pageNumber, id, searchString);
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
    this.lastSearchPerformance = 'Suche ...';
    const time = performance.now();
    this.lastSearchSubscription = obs.subscribe(page => {
      this.titleFor = searchString ? `für ${searchEntityType.getNameSingular()}='${searchString}'` : 'insgesamt';
      this.titleService.setTitle(`${this.entityType.namePlural}${searchString || id ? ' für ' + searchString || id : ''}`);
      this.fillEntityList(page, searchEntityType, id, searchString);
      const timeString = (performance.now() - time).toFixed(2);
      this.lastSearchPerformance = this.getSearchMessage(searchEntityType, id, searchString) + ` dauerte ${timeString}ms`;
      console.log(this.lastSearchPerformance);
    });
  }

  private getSearchMessage = (searchEntityType: typeof AbstractEntity, id: Number | undefined, searchString: string | undefined) => {
    return `Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName + (id ? ' id ' + id : '')}=${searchString || '*'}`;
  }

  private fillEntityList(page: Page<E>, searchEntityType: typeof AbstractEntity, searchId?: Number, searchString?: string) {
    this.page = page;
    this.searchEntityType = searchEntityType;
    this.lastSearchId = searchId;
    this._searchText = searchString || '';
    //TODO offenbar noch zu früh, wenn Liste gefüllt - DOM muss erst geändert sein!
    setTimeout(() => {
      this.scrollToLastSelectedElement();
    }, 200);
  }

  private scrollToLastSelectedElement() {
    //Element, von dem wir vorher gekommen sind
    if (this.lastClickedEntity) {
      const id = EntityComponent.getIdForEntity(this, this.lastClickedEntity);
      const element = document.getElementById(id);
      if (element) {
        console.log(`Scroll zu ${this.lastClickedEntity.name} mit html.id=${id}`);
        element.scrollIntoView({behavior: 'smooth'});
      } else {
        console.log(`Element ${this.lastClickedEntity.name} für scroll nicht vorhanden`);
      }
    }
  }

  searchPreviousPage(): void {
    this.loadPage(-1);
  }

  searchNextPage(): void {
    this.loadPage(+1);
  }

  loadPage(add: number = 0): void {
    if (this.searchEntityType) {
      let pageNumber = this.page?.number || 0;
      if (this.page) {
        pageNumber = Math.min(Math.max(this.page.number + add, 0), this.page.totalPages - 1);
      }
      this.searchByEntityIdOrName(this.searchEntityType, pageNumber, this.lastSearchId, this._searchText);
    }
  }

  navigateOtherEntityByThis(entityType: typeof AbstractEntity, entity: AbstractEntity) {
    this.navigateOtherEntityBy(entityType, this.entityType, entity, entity);
  }

  navigateOtherEntityByItself(entityType: typeof AbstractEntity, entity: AbstractEntity,
                              sourceEntity?: AbstractEntity) {
    this.navigateOtherEntityBy(entityType, entityType, entity, sourceEntity);
  }

  /**
   * Sucht mittels einer gegebenen Entity als Suchkriterium in einer anderen Entity-Liste.
   * Beim Öffnen der entsprechenden View (anderen EntityListComponent) wird dann über die
   * queryParams die entsprechende Suche ausgelöst.
   * @param entityType der Typ, zu dem navigiert wird
   * @param searchEntityType der Typ, anhand dem gesucht werden soll
   * @param entity eine Entity des searchEntityType, nach der gesucht wird (anhand id)
   * @param sourceEntity Quell-Entity, von der aus geklickt wurde
   */
  navigateOtherEntityBy(entityType: typeof AbstractEntity,
                        searchEntityType: typeof AbstractEntity, entity: AbstractEntity,
                        sourceEntity?: AbstractEntity) {
    console.log(`Navigiere nach ${entityType.entityName}/${searchEntityType.entityName}/${entity.id}='${entity.name}'`);
    const state = this.storeState(entity, sourceEntity);
    this.router.navigate([entityType.entityName, searchEntityType.entityName, entity.id],
      { onSameUrlNavigation: 'reload', state: state});
  }

  navigateToRoot(searchString: string) {
    console.log(`Gehe wieder zu root ${this.entityType.entityName}/${this.searchEntityType.entityName}'`);
    //entity & sourceEntity verwerfen
    const state = this.storeState(undefined, undefined);
    state[paramSearchText] = searchString;
    this.router.navigate([this.entityType.entityName],
      { onSameUrlNavigation: 'ignore', state: state });
  }

  navigateToDetails(entity: AbstractEntity) {
    console.log(`Navigiere zu Details für ${this.entityType.entityName} id ${entity.id}='${entity.name}'`);
    const state = this.storeState(undefined, entity);
    this.router.navigate([this.entityType.entityName, entity.id, detailsPath],
      { onSameUrlNavigation: 'reload', state: state});
  }

  private storeState(entity?: AbstractEntity, sourceEntity?: AbstractEntity) {
    const state: Params = {};
    state[paramEntity] = entity;
    state[paramSourceEntity] = sourceEntity;
    state[paramSearchEntity] = this.searchFieldComponent.searchEntity.entityName;
    state[paramSearchText] = this.searchText;
    state[paramFilterText] = this.filterText;
    state[paramPageNumber] = this.page?.number;
    state[paramPageSize] = this.pageSize;
    return state;
  }

  removeEntity(entity: E) {
    this.service.removeById(this.entityType, entity.id).subscribe({
        next: () => {
          console.log(`Löschen von ${entity.id}=${entity.name} erfolgreich`);
          //this.page!.content = this.page!.content.filter(e => e !== entity);
          this.loadPage();
        },
        error: err => {
          console.log(`Löschen von ${entity.id}=${entity.name} ERROR`, err);
        }
      })
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
    this.searchByEntityName(this.searchEntityType, this.searchText);
  }

  get searchText(): string {
    return this._searchText;
  }

  set searchText(value: string) {
    this._searchText = value;
    if (this.route.snapshot.params[paramId]) {
      //wir haben schon nach ID usw. gesucht => müssen jetzt auf die Basisseite
      this.navigateToRoot(value);
    } else {
      this.searchByEntityName(this.searchEntityType, value);
    }
  }

  get filterText(): string {
    return this._filterText;
  }

  set filterText(value: string) {
    this._filterText = value.toLowerCase();
  }

  get listTitle() {
    if (!this.page || !this.entityType) {
      return '';
    }
    let title;
    const entityCount = this.getEntities().filter(e => this.isEntityShown(e)).length;
    if (this._filterText && entityCount < this.page.numberOfElements) {
      title = `${entityCount} von ${this.entityType.getNumberDescription(this.page.totalElements)}`;
    } else {
      const entityStart = this.page.number * this.page.size;
      title = entityCount !== this.page.totalElements
        //verwende hier this.page.content.length statt this.page.numberOfElements da wir evtl. selber rauslöschen
        ? `${this.entityType.getNumbersDescription(entityStart+1, this.page.content.length + entityStart)} von ${this.page.totalElements}`
        : this.entityType.getNumberDescription(this.page.totalElements);
    }
    return `${title} ${this.titleFor}`;
  }

  getEntities(): E[] {
    return this.page?.content || [];
  }

  getEntityId(index: any, entity: AbstractEntity): number {
    return entity.id;
  }

  isEntityFiltered(entity: AbstractEntity): boolean {
    return !!this._filterText && !entity.name?.toLowerCase().includes(this._filterText);
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

  protected readonly EntityComponent = EntityComponent;
}
