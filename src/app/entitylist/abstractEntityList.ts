import {Component, OnInit} from '@angular/core';
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

@Component({
  template: ''
})
export abstract class AbstractEntityList<E extends AbstractEntity> implements OnInit {
  public static urlParamEntityName = 'entityName';

  protected _title!: string;
  protected _entities!: E[];
  protected _entityName: string;
  protected _entityNamePlural!: string;

  private static searchEntities: typeof AbstractEntity[] = [Album, Track, Komponist, Werk, Genre, Interpret];
  private _searchableEntities;

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute,
              protected router: Router) {
    this._entityName = service.entityName;
    this._entityNamePlural = service.entityNamePlural;

    this._searchableEntities = AbstractEntityList.searchEntities.filter(
      (entity) => entity.entityName != this._entityName
    );

    console.log(`${this._entityName}List created`);
  }

  public ngOnInit(): void {
    const queryParamMap = this.route.snapshot.queryParamMap;
    for (const ent of this.searchableEntities) {
      const id = queryParamMap.get(ent.entityName);
      if (id) {
        const searchName = queryParamMap.get(AbstractEntityList.urlParamEntityName) || undefined;
        console.log(`Suche ${this._entityNamePlural} nach ${ent.entityName}=${id}`);
        const obs = this.service.findBy(ent.entityName + 'Id', id); //this.service.get(id);
        const time = performance.now();
        obs.pipe().subscribe(data => {
          this.extractData(data, searchName, id);
          console.log(`dauerte ${performance.now() - time}ms`);
        });
        //nicht weitersuchen
        return;
      }
    }
    //default: alle anzeigen
    this.searchForName();
  }

  protected searchForName(searchText?: string) {
    console.log(`Suche ${this._entityNamePlural} nach ${searchText}`);
    const obs = this.service.find(searchText || '');
    const time = performance.now();
    obs.pipe().subscribe(data => {
      this.extractData(data, searchText);
      console.log(`dauerte ${performance.now() - time}ms`);
    });
  }

  //TODO statt Listen Streams? Titel erst nach Erhalt aller Daten möglich!
  protected extractData(data: E[], entityName?: string, id?: string) {
    this._title = `${data.length} ${this._entityNamePlural}${entityName ? ' für ' + entityName : id ? ' für Id=' + id : ''}`;
    this._entities = data;
  }

  public searchOtherEntityByThis(entityName: string, name: string, id: number) {
    this.searchOtherEntityBy(entityName, this._entityName, name, id);
  }

  public searchOtherEntityBy(entityName: string, searchEntityName: string, name: string, id: number) {
    console.log(`search ${entityName} nach ${searchEntityName}=${name},${id}`);
    const params = new HttpParams()
      .set(searchEntityName, id)
      .set(AbstractEntityList.urlParamEntityName, name);
    this.router.navigateByUrl(entityName + '?' + params.toString());
  }

  public get title() {
    return this._title;
  }

  public get entities(): E[] {
    return this._entities;
  }

  get searchableEntities() {
    return this._searchableEntities;
  }
}
