import {Component, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Subscription} from 'rxjs';

@Component({
  template: '',
})
export abstract class AbstractEntityList<E extends AbstractEntity> implements OnInit {
  private title: string = '';
  private _entities: E[] = [];
  private _name: string = '';
  private _namePlural: string = '';

  constructor(private service: AbstractEntityService<E>) {
    this._name = service.entityName;
    console.log(`ListComponent for ${this._name} created`);
  }

  set namePlural(value: string) {
    this._namePlural = value;
  }

  get entities(): E[] {
    return this._entities;
  }

  public search(searchText: string): Subscription {
    console.log(`Suche ${this._namePlural} nach ${searchText}`);
    const obs = this.service.find(searchText);
    return obs.subscribe(data => {
      console.log('Setting data');
      this.title = `${data.length} ${this._namePlural}${searchText ? ' für ' + searchText : ''}`;
      this._entities = data;
      for (const ent of this._entities) {
        ent.alben = `http://localhost:8080/musik/album/get?${this._name}Id=${ent.id}`;
      }
    });
  }

  getTitle() {
    return this.title;
  }

  ngOnInit(): void {
    this.search('');
  }
}
