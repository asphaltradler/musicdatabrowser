import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntityService} from './abstractEntityService';
import {Album} from '../entities/album';
import {Observable, tap} from 'rxjs';
import {Page} from '../entities/page';
import {AlbumModel} from '../entitymodels/album-model';

@Injectable({
  providedIn: 'root'
})
export class AlbumService extends AbstractEntityService<Album> {
  constructor(http: HttpClient) {
    super(http, Album);
  }

  protected override getPage(url: string, params: HttpParams): Observable<Page<Album>> {
    return this.http.get<Page<Album>>(url, {params}).pipe(tap(
      page => {
        page.modelContent = page.content.map(e => new AlbumModel(e));
      }));
  }

}
