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

  protected _title!: string;
  protected _entities!: E[];

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

  searchByEntityName(searchEntityType: typeof AbstractEntity, searchString?: string) {
    this.searchByEntityIdOrName(searchEntityType, undefined, searchString);
  }

  searchByEntityId(searchEntityType: typeof AbstractEntity, id: number, searchString?: string) {
    this.searchByEntityIdOrName(searchEntityType, id, searchString);
  }

  private searchByEntityIdOrName(searchEntityType: typeof AbstractEntity, id?: Number, searchString?: string) {
    //falls noch eine Suche unterwegs ist: abbrechen
    this.lastSearchSubscription?.unsubscribe();
    console.log(`Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName}=${searchString || '*'}`);
    const obs = id
      ? this.service.findByOtherId(searchEntityType.entityName, id.valueOf())
      : this.service.findByOtherNameLike(searchEntityType.entityName, searchString || '');
    const time = performance.now();
    this.lastSearchSubscription = obs.subscribe(data => {
      this.fillEntities(data, searchString ? `für ${searchEntityType.getNameSingular()} '${searchString}'` : 'insgesamt');
      console.log(`Suche ${this.entityType.namePlural} nach ${searchEntityType.entityName} dauerte ${performance.now() - time}ms`);
    });
  }

  //TODO statt Listen Streams? Wir müssten dann paginieren
  fillEntities(data: E[], titleName?: string) {
    this._title = `${this.entityType.getNumberDescription(data.length)} ${titleName}`;
    this._entities = data;
  }

  searchOtherEntityByThis(entityName: string, entity: AbstractEntity) {
    this.searchOtherEntityBy(entityName, this.entityType.entityName, entity);
  }

  searchOtherEntityByItself(entityName: string, entity: AbstractEntity) {
    this.searchOtherEntityBy(entityName, entityName, entity);
  }

  searchOtherEntityBy(entityName: string, searchEntityName: string, entity: AbstractEntity) {
    const params: Params = {};
    params[EntityListComponent.urlParamEntityName] = searchEntityName;
    params[EntityListComponent.urlParamEntitySearchTitle] = entity.name;
    params['id'] = entity.id;
    console.log(`Navigiere nach ${entityName} mit ${Object.entries(params).join('|')}`);
    this.router.navigate([entityName], {queryParams: params});
  }

  trackByItemId(_index: number, item: E): number {
    return item.id;
  }

  get title() {
    return this._title;
  }

  get entities(): E[] {
    return this._entities;
  }

  get searchableEntities() {
    return this._searchableEntities;
  }
}
