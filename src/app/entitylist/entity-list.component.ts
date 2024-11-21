import {OnDestroy, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, EventType, Params, Router} from '@angular/router';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Subscription} from 'rxjs';
import {SearchfieldComponent} from '../search/searchfield.component';

@Object
export abstract class EntityListComponent<E extends AbstractEntity> implements OnInit, OnDestroy {
  public static urlParamEntitySearchTitle = 'title';
  public static urlParamEntityName = 'searchby';

  public entityType!: typeof AbstractEntity;

  protected _entities: E[] = [];
  protected filter = '';
  protected titleFor = '';
  protected lastSearchNameForThis = '';

  private changeSubscription!: Subscription;
  private lastSearchSubscription!: Subscription;

  protected _searchableEntities;

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute,
              protected router: Router) {
    this.entityType = service.entityType;
    //default/Vorbelegung
    this._searchableEntities = SearchfieldComponent.searchEntities.filter(
      (entity) => entity != this.entityType
    );
    this.changeSubscription = router.events.subscribe((event) => {
      if (event.type === EventType.ActivationEnd) {
        this.startSearchFromQuery();
      }
    });
    console.log(`${this.entityType.getNameSingular()}List created`);
  }

  ngOnInit(): void {
    //erledigt die Change-Subscription schon
  }

  ngOnDestroy(): void {
    this.changeSubscription?.unsubscribe();
    this.lastSearchSubscription?.unsubscribe();
    this.filter = '';
    this.lastSearchNameForThis = '';
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
      this.searchByEntityIdOrName(this.entityType);
    }
  }

  searchByEntityName(searchEntityType: typeof AbstractEntity, searchString: string) {
    if (searchEntityType === this.entityType) {
      if (searchString.includes(this.lastSearchNameForThis)) {
        this.filter = searchString;
        console.log(`Keine neue Suche, da Suchtext '${searchString}' im vorherigen '${this.lastSearchNameForThis}' enthalten`);
        //this.lastSearchNameForThis = searchString;
        return;
      }
    }
    this.searchByEntityIdOrName(searchEntityType, undefined, searchString);
  }

  searchByEntityId(searchEntityType: typeof AbstractEntity, id: number, searchString: string) {
    this.searchByEntityIdOrName(searchEntityType, id, searchString);
  }

  private searchByEntityIdOrName(searchEntityType: typeof AbstractEntity, id?: Number, searchString?: string) {
    //falls noch eine Suche unterwegs ist: abbrechen
    this.lastSearchSubscription?.unsubscribe();
    console.log(`Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName}=${searchString || '*'}`);
    const obs = id
      ? this.service.findByOtherId(searchEntityType, id.valueOf())
      : this.service.findByOtherNameLike(searchEntityType, searchString || '');
    const time = performance.now();
    this.lastSearchSubscription = obs.subscribe(data => {
      this.titleFor = searchString ? `für ${searchEntityType.getNameSingular()} '${searchString}'` : 'insgesamt';
      this.fillData(data);
      console.log(`Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName} dauerte ${performance.now() - time}ms`);
    });
  }

  fillData(data: E[]) {
    this._entities = data;
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
    if (this.filter) {
      const filteredCount = this._entities.filter((ent) => ent.name.toLowerCase().includes(this.filter)).length;
      return `${filteredCount} von ${this.entityType.getNumberDescription(this._entities.length)} ${this.titleFor}`;
    } else {
      return `${this.entityType.getNumberDescription(this._entities.length)} ${this.titleFor}`;
    }
  }

  get entities(): E[] {
    return this.filter
      ? this._entities.filter((ent) => ent.name.toLowerCase().includes(this.filter))
      : this._entities;
  }

  get searchableEntities() {
    return this._searchableEntities;
  }
}
