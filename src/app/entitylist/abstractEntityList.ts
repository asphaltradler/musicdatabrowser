import {Component, OnInit} from '@angular/core';
import {AbstractEntity} from '../entities/abstractEntity';
import {AbstractEntityService} from '../services/abstractEntityService';
import {Observable, Subscription} from 'rxjs';

@Component({
  template: '',
})
export abstract class EntityListComponent<E extends AbstractEntity> implements OnInit {
  private title: string = "";
  private _entities: E[] = [];
  private _name: string = "";
  private _namePlural: string = "";

  constructor(private service: AbstractEntityService<E>) {}

  set name(value: string) {
    this._name = value;
  }

  set namePlural(value: string) {
    this._namePlural = value;
  }

  get entities(): E[] {
    return this._entities;
  }

  public search(searchText: string): Observable<E[]> {
    console.log("Suche " + this._namePlural + " nach " + searchText);
    const obs = this.service.find(searchText);
    obs.subscribe(data => {
      this.title = "Alle " + this._namePlural + (searchText ? " für " + searchText : "");
      this._entities = data;
    });
    return obs;
  }

  getTitle() {
    return this.title;
  }

  ngOnInit(): void {
    this.search('');
  }
}
