import {Injectable} from '@angular/core';
import {AbstractEntityService} from './abstractEntityService';
import {Genre} from '../entities/genre';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GenreService extends AbstractEntityService<Genre> {
  constructor(http: HttpClient) {
    super(http, Genre.entityName, Genre.namePlural);
  }
}
