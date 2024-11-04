import {Component, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {Album} from '../entities/album';

@Component({
  template: '',
})
export abstract class AbstractEntityList<E extends AbstractEntity> implements OnInit {
  protected routeUrl = location.origin;
  protected title: string = '';
  protected _entities: E[] = [];
  protected _name: string = '';
  protected _namePlural: string = '';

  constructor(protected service: AbstractEntityService<E>,
              protected route: ActivatedRoute) {
    this._name = service.entityName;
    console.log(`ListComponent for ${this._name} created`);
  }

  set namePlural(value: string) {
    this._namePlural = value;
  }

  get entities(): E[] {
    return this._entities;
  }

  public search(searchText?: string): Subscription {
    console.log(`Suche ${this._namePlural} nach ${searchText}`);
    const obs = this.service.find(searchText);
    return obs.subscribe(data => {
      console.log('Setting data');
      this.title = `${data.length} ${this._namePlural}${searchText ? ' für ' + searchText : ''}`;
      this._entities = data;
      this.fillData();
    });
  }

  protected fillData() {
    for (const ent of this._entities) {
      //TODO über Router erstellen
      ent.alben = `${this.routeUrl}/${Album.name}?${this._name.toLowerCase()}Id=${ent.id}`;
    }
  }

  getTitle() {
    return this.title;
  }

  ngOnInit(): void {
    const id = this.route.snapshot.queryParamMap.get('id');
    if (id) {
      console.log(`Suche ${this._namePlural} nach Id=${id}`);
      const obs = this.service.get(id);
      obs.subscribe(data => {
        console.log('Setting data');
        this.title = `${data.length} ${this._namePlural}${id ? ' für Id=' + id : ''}`;
        this._entities = data;
        this.fillData();
      });
    } else {
      this.search();
    }
   }
}
