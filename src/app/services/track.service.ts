import {Injectable} from '@angular/core';
import {AbstractEntityService} from './abstractEntityService';
import {Track} from '../entities/track';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TrackService extends AbstractEntityService<Track> {

  constructor(http: HttpClient) {
    super(http, Track);
  }
}
