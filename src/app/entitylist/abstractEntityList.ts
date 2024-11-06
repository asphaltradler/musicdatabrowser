import {Component, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute} from '@angular/router';
import {Album} from '../entities/album';
import {Subscription} from 'rxjs';
import {HttpParams} from '@angular/common/http';
import {AbstractEntityService} from '../services/abstractEntityService';

@Component({
  template: ''
})
export abstract class AbstractEntityList<E extends AbstractEntity> implements OnInit {
  public static urlParamEntityName = 'entityName';

  protected albumUrl: string;
  protected _title!: string;
  protected _entities: E[] = [];
  protected _entityName: string;
  protected _entityNamePlural!: string;

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute) {
    this._entityName = service.entityName;

    this.albumUrl = location.origin + '/' + Album.name + '?';
    route.title.subscribe(title => this._entityNamePlural = title || '');
    console.log(`${this._entityName} created`);
  }

  public ngOnInit(): void {
    this.searchForId('id');
  }

  protected searchForId(idKey: string) {
    const queryParamMap = this.route.snapshot.queryParamMap;
    const id = queryParamMap.get(idKey);
    const entityName = queryParamMap.get(AbstractEntityList.urlParamEntityName) || undefined;
    if (id) {
      console.log(`Suche ${this._entityNamePlural} nach ${idKey}=${id}`);
      const obs = this.service.findBy(idKey, <string>id); //this.service.get(id);
      obs.subscribe(data => {
        this.extractData(data, entityName, id);
      });
    } else {
      this.searchForName();
    }
  }

  protected searchForName(searchText?: string): Subscription {
    console.log(`Suche ${this._entityNamePlural} nach ${searchText}`);
    const obs = this.service.find(searchText);
    return obs.subscribe(data => {
      this.extractData(data, searchText);
    });
  }

  protected extractData(data: E[], entityName?: string, id?: string) {
    console.log('Setting data');
    this._title = `${data.length} ${this._entityNamePlural}${entityName ? ' für ' + entityName : id ? ' für Id=' + id : ''}`;
    this._entities = data;
    this.createLinks();
  }

  protected createLinks() {
    for (const ent of this._entities) {
      //TODO über Router erstellen?
      const params = new HttpParams()
        .set(this._entityName.toLowerCase() + 'Id', ent.id)
        .set(AbstractEntityList.urlParamEntityName, ent.name);
      ent.linkToAlben = this.albumUrl + params.toString();
    }
  }

  public get title() {
    return this._title;
  }

  public get entities(): E[] {
    return this._entities;
  }
}
