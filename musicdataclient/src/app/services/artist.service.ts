import {Injectable} from '@angular/core';
import {AbstractEntityService} from './abstractEntityService';
import {HttpClient} from '@angular/common/http';
import {Artist} from '../entities/artist';

@Injectable({
  providedIn: 'root'
})
export class ArtistService extends AbstractEntityService<Artist> {
  constructor(http: HttpClient) {
    super(http, Artist);
  }
}
