import {OnDestroy, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, EventType, Params, Router} from '@angular/router';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Subscription} from 'rxjs';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {Page} from '../entities/page';
import {appDefaults} from '../../config/config';

@Object
export abstract class EntityListComponent<E extends AbstractEntity> implements OnInit, OnDestroy {
  public static urlParamEntitySearchTitle = 'title';
  private static urlParamEntityName = 'searchby';

  public entityType!: typeof AbstractEntity;
  public page?: Page<E>;
  public pageSize = appDefaults.defaultPageSize;

  protected filter = '';
  protected titleFor = '';
  protected lastSearchNameForThis?: string = undefined;

  private changeSubscription: Subscription;
  private lastSearchSubscription?: Subscription;

  private lastSearchEntityType?: typeof AbstractEntity;
  private lastSearchId?: Number;
  private lastSearchName?: string;

  protected _searchableEntities: typeof AbstractEntity[];

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute,
              protected router: Router) {
    this.entityType = service.entityType;
    //eigenen Typ ausschließen in Darstellung
    this._searchableEntities = SearchfieldComponent.searchEntities.filter(
      (entity) => entity != this.entityType
    );
    //default/Vorbelegung bei Aktivierung
    this.changeSubscription = router.events.subscribe((event) => {
      if (event.type === EventType.ActivationEnd) {
        //zurücksetzen, damit wir neu suchen
        this.lastSearchNameForThis = undefined;
        this.startSearchFromQuery();
      }
    });
    console.log(`${this.entityType.getNameSingular()}List created`);
  }

  ngOnInit(): void {
    //wird durch ActivationEnd-Event erledigt
  }

  ngOnDestroy(): void {
    this.changeSubscription?.unsubscribe();
    this.lastSearchSubscription?.unsubscribe();
    this.filter = '';
    this.lastSearchNameForThis = undefined;
  }

  startSearchFromQuery(): void {
    const queryParamMap = this.route.snapshot.queryParamMap;
    const searchEntityName = queryParamMap.get(EntityListComponent.urlParamEntityName);
    if (searchEntityName) {
      const ent = SearchfieldComponent.searchEntities.find(e => e.entityName === searchEntityName);
      if (ent) {
        const id = queryParamMap.get('id');
        const searchString = queryParamMap.get(EntityListComponent.urlParamEntitySearchTitle);
        if (id) {
          this.searchByEntityId(ent, Number.parseInt(id || '-1'), searchString || '');
        } else if (searchString) {
          this.searchByEntityName(ent, searchString || '');
        }
      }
    } else {
      //default: alle anzeigen
      this.searchByEntityName(this.entityType, '');
    }
  }

  searchByEntityName(searchEntityType: typeof AbstractEntity, searchString: string) {
    if (appDefaults.useLocalFilteringInsteadSearch
      && searchEntityType === this.entityType) {
      if (this.lastSearchNameForThis !== undefined && searchString.toLowerCase().includes(this.lastSearchNameForThis)) {
        this.filter = searchString.toLowerCase();
        console.log(`Keine neue Suche, da Suchtext '${searchString}' im vorherigen '${this.lastSearchNameForThis}' enthalten`);
        return;
      }
      this.lastSearchNameForThis = searchString.toLowerCase();
    }
    this.searchByEntityIdOrName(searchEntityType, 0, undefined, searchString);
  }

  searchByEntityId(searchEntityType: typeof AbstractEntity, id: number, searchString: string) {
    this.searchByEntityIdOrName(searchEntityType, 0, id, searchString);
  }

  private searchByEntityIdOrName(searchEntityType: typeof AbstractEntity, pageNumber: number, id?: Number, searchString?: string) {
    //falls noch eine Suche unterwegs ist: abbrechen
    this.lastSearchSubscription?.unsubscribe();
    console.log(`Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName}=${searchString || '*'}`);
    const obs = id
      ? this.service.findByOtherId(searchEntityType, id.valueOf(), pageNumber, this.pageSize)
      : this.service.findByOtherNameLike(searchEntityType, searchString?.toLowerCase() || '', pageNumber, this.pageSize);
    const time = performance.now();
    this.lastSearchSubscription = obs.subscribe(data => {
      this.titleFor = searchString ? `für ${searchEntityType.getNameSingular()}='${searchString}'` : 'insgesamt';
      this.fillData(data, searchEntityType, id, searchString);
      console.log(`Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName} dauerte ${performance.now() - time}ms`);
    });
  }

  fillData(data: Page<E>, searchEntityType: typeof AbstractEntity, searchId?: Number, searchString?: string) {
    this.page = data;
    this.lastSearchEntityType = searchEntityType;
    this.lastSearchId = searchId;
    this.lastSearchName = searchString;
  }

  setPageSize(value: number) {
    this.pageSize = value;
  }

  searchPreviousPage(): void {
    if (this.lastSearchEntityType && this.hasPreviousPage()) {
      this.searchByEntityIdOrName(this.lastSearchEntityType, this.page!.number - 1, this.lastSearchId, this.lastSearchName);
    }
  }

  searchNextPage(): void {
    if (this.lastSearchEntityType && this.hasNextPage()) {
      this.searchByEntityIdOrName(this.lastSearchEntityType, this.page!.number + 1, this.lastSearchId, this.lastSearchName);
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
    const params: Params = {};
    params[EntityListComponent.urlParamEntityName] = searchEntityType.entityName;
    params[EntityListComponent.urlParamEntitySearchTitle] = entity.name;
    params['id'] = entity.id;
    console.log(`Navigiere nach ${entityType.entityName} mit ${Object.entries(params).join('|')}`);
    this.router.navigate([entityType.entityName], {queryParams: params});
  }

  trackByItemId(_index: number, item: E): number {
    return item.id;
  }

  setFilter(filterString: string) {
    this.filter = filterString.toLowerCase();
  }

  get title() {
    if (!this.page) {
      return '';
    }
    let title;
    const entityCount = this.entities.length;
    if (this.filter && entityCount < this.page.numberOfElements) {
      title = `${entityCount} von ${this.entityType.getNumberDescription(this.page.totalElements)}`;
    } else {
      const entityStart = this.page.number * this.page.size;
      title = entityCount !== this.page.totalElements
        ? `${this.entityType.getNumbersDescription(entityStart+1, this.page.numberOfElements + entityStart)} von ${this.page.totalElements}`
        : this.entityType.getNumberDescription(this.page.totalElements);
    }
    return `${title} ${this.titleFor}`;
  }

  get entities(): E[] {
    if (!this.page) {
      return [];
    }
    return this.filter
      ? this.page.content.filter((ent) => ent.name?.toLowerCase().includes(this.filter))
      : this.page.content;
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
