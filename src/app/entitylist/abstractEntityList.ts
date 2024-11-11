import {Component, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, Router} from '@angular/router';
import {Album} from '../entities/album';
import {Subscription} from 'rxjs';
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
  protected _entities: E[] = [];
  protected _entityName: string;
  protected _entityNamePlural!: string;

  private static searchEntities = [Album, Track, Komponist, Werk, Genre, Interpret];
  private _searchableEntities;

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute,
              protected router: Router) {
    this._entityName = service.entityName;
    this._entityNamePlural = service.entityNamePlural;

    this._searchableEntities = AbstractEntityList.searchEntities.filter(
      (name) => name.entityName != this._entityName
    );

    //route.title.subscribe(title => this._entityNamePlural = title || '');
    console.log(`${this._entityName}List created`);
  }

  public ngOnInit(): void {
    const queryParamMap = this.route.snapshot.queryParamMap;
    for (const ent of this.searchableEntities) {
      const key = ent.entityName + 'Id';
      if (queryParamMap.has(key)) {
        this.searchForId(key);
        //nicht weitersuchen
        return;
      }
    }
   this.searchForId('id');
  }

  protected searchForId(idKey: string) {
    const queryParamMap = this.route.snapshot.queryParamMap;
    const id = queryParamMap.get(idKey);
    const entityName = queryParamMap.get(AbstractEntityList.urlParamEntityName) || undefined;
    if (id) {
      console.log(`Suche ${this._entityNamePlural} nach ${idKey}=${id}`);
      const obs = this.service.findBy(idKey, <string>id); //this.service.get(id);
      obs.pipe().subscribe(data => {
        this.extractData(data, entityName, id);
      });
    } else {
      this.searchForName();
    }
  }

  protected searchForName(searchText?: string): Subscription {
    console.log(`Suche ${this._entityNamePlural} nach ${searchText}`);
    const obs = this.service.find(searchText);
    return obs.pipe().subscribe(data => {
      this.extractData(data, searchText);
    });
  }

  //TODO statt Listen Streams? Titel erst nach Erhalt aller Daten möglich!
  protected extractData(data: E[], entityName?: string, id?: string) {
    console.log('Setting data');
    this._title = `${data.length} ${this._entityNamePlural}${entityName ? ' für ' + entityName : id ? ' für Id=' + id : ''}`;
    this._entities = data;
  }

  public searchOtherEntityByThis(entityName: string, name: string, id: number) {
    console.log(`search ${entityName} nach ${this._entityName}=${name},${id}`);
    const params = new HttpParams()
      .set(this._entityName + 'Id', id)
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
