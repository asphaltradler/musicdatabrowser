import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AbstractEntityService} from './abstractEntityService';
import {Album} from '../entities/album';

@Injectable({
  providedIn: 'root'
})
export class AlbumService extends AbstractEntityService<Album> {
  constructor(http: HttpClient) {
    super(http, 'http://localhost:8080/musik/album/get', 'album');
  }
}
