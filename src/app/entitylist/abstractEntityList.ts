import {OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, Router} from '@angular/router';
import {Album} from '../entities/album';
import {HttpParams} from '@angular/common/http';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Komponist} from '../entities/komponist';
import {Werk} from '../entities/werk';
import {Interpret} from '../entities/interpret';
import {Genre} from '../entities/genre';
import {Track} from '../entities/track';

@Object
export abstract class AbstractEntityList<E extends AbstractEntity> implements OnInit {
  public static urlParamEntityName = 'entityName';

  protected _title!: string;
  protected _entities!: E[];
  protected entityType: typeof AbstractEntity;

  private static searchEntities: typeof AbstractEntity[] = [Album, Track, Komponist, Werk, Genre, Interpret];
  protected _searchableEntities;

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute,
              protected router: Router) {
    this.entityType = service.entityType;
    this._searchableEntities = AbstractEntityList.searchEntities.filter(
      (entity) => entity != this.entityType
    );

    console.log(`${this.entityType.getNameSingular()}List created`);
  }

  ngOnInit(): void {
    const queryParamMap = this.route.snapshot.queryParamMap;
    for (const ent of AbstractEntityList.searchEntities) {
      const id = queryParamMap.get(ent.entityName);
      if (id) {
        const searchName = queryParamMap.get(AbstractEntityList.urlParamEntityName) || undefined;
        console.log(`Suche ${this.entityType.namePlural} nach ${ent.entityName}=${id}`);
        const obs = this.service.findBy(ent.entityName + 'Id', id); //this.service.get(id);
        const time = performance.now();
        obs.pipe().subscribe(data => {
          this.fillEntities(data, `für ${ent.getNameSingular()} '${searchName}'`);
          console.log(`dauerte ${performance.now() - time}ms`);
        });
        //nicht weitersuchen
        return;
      }
    }
    //default: alle anzeigen
    this.searchForName();
  }

  searchForName(searchText?: string) {
    console.log(`Suche ${this.entityType.namePlural} nach ${searchText ? searchText : '*'}`);
    const obs = this.service.find(searchText || '');
    const time = performance.now();
    obs.pipe().subscribe(data => {
      this.fillEntities(data, searchText ? `für Name '${searchText}'` : 'insgesamt');
      console.log(`dauerte ${performance.now() - time}ms`);
    });
  }

  //TODO statt Listen Streams? Titel erst nach Erhalt aller Daten möglich!
  fillEntities(data: E[], titleName?: string) {
    this._title = `${this.service.entityType.getNumberDescription(data.length)} ${titleName}`;
    this._entities = data;
  }

  searchOtherEntityByThis(entityName: string, entity: AbstractEntity) {
    this.searchOtherEntityBy(entityName, this.entityType.entityName, entity);
  }

  searchOtherEntityByItself(entityName: string, entity: AbstractEntity) {
    this.searchOtherEntityBy(entityName, entityName, entity);
  }

  searchOtherEntityBy(entityName: string, searchEntityName: string, entity: AbstractEntity) {
    console.log(`search ${entityName} nach ${searchEntityName}=${entity.name},${entity.id}`);
    const params = new HttpParams()
      .set(searchEntityName, entity.id)
      .set(AbstractEntityList.urlParamEntityName, entity.name);
    this.router.navigateByUrl(entityName + '?' + params.toString());
  }

  trackByItemId(index: number, item: E): number {
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
