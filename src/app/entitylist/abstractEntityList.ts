import {OnDestroy, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, EventType, Params, Router} from '@angular/router';
import {Album} from '../entities/album';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Komponist} from '../entities/komponist';
import {Werk} from '../entities/werk';
import {Interpret} from '../entities/interpret';
import {Genre} from '../entities/genre';
import {Track} from '../entities/track';
import {Subscription} from 'rxjs';

@Object
export abstract class AbstractEntityList<E extends AbstractEntity> implements OnInit, OnDestroy {
  public static urlParamEntitySearchTitle = 'title';
  public static urlParamEntityName = 'searchby';

  protected _title!: string;
  protected _entities!: E[];
  protected entityType: typeof AbstractEntity;

  private changeSubscription!: Subscription;
  private lastSearchSubscription!: Subscription;

  private static searchEntities: typeof AbstractEntity[] = [Album, Track, Komponist, Werk, Genre, Interpret];
  protected _searchableEntities;

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute,
              protected router: Router) {
    this.entityType = service.entityType;
    this._searchableEntities = AbstractEntityList.searchEntities.filter(
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
    const searchEntityName = queryParamMap.get(AbstractEntityList.urlParamEntityName);
    const id = queryParamMap.get('id');
    if (searchEntityName && id) {
      const ent = AbstractEntityList.searchEntities.find(e => e.entityName === searchEntityName);
      if (ent) {
          //falls noch eine Suche unterwegs ist: abbrechen
          this.lastSearchSubscription?.unsubscribe();
          const searchName = queryParamMap.get(AbstractEntityList.urlParamEntitySearchTitle) || '';
          const obs = this.service.findBy(ent.entityName, id); //this.service.get(id);
          const time = performance.now();
          this.lastSearchSubscription = obs.subscribe(data => {
            this.fillEntities(data, `für ${ent.getNameSingular()} '${searchName}'`);
            console.log(`Suche ${this.entityType.namePlural} nach ${ent.entityName}=${id} dauerte ${performance.now() - time}ms`);
          });
        }
    } else {
      //default: alle anzeigen
      this.searchForName();
    }
  }

  searchForName(searchText?: string) {
    console.log(`Suche ${this.entityType.namePlural} nach ${searchText || '*'}`);
    this.lastSearchSubscription?.unsubscribe();
    const obs = this.service.find(searchText || '');
    const time = performance.now();
    this.lastSearchSubscription = obs.subscribe(data => {
      this.fillEntities(data, searchText ? `für Name '${searchText}'` : 'insgesamt');
      console.log(`dauerte ${performance.now() - time}ms`);
    });
  }

  //TODO statt Listen Streams? Wor müssten dann paginieren
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
    params[AbstractEntityList.urlParamEntityName] = searchEntityName;
    params[AbstractEntityList.urlParamEntitySearchTitle] = entity.name;
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
