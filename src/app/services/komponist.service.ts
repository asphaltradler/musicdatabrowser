import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Komponist} from '../entities/komponist';
import {AbstractEntityService} from './abstractEntityService';
import {Album} from '../entities/album';

@Injectable({
  providedIn: 'root'
})
export class KomponistService extends AbstractEntityService<Komponist> {
  constructor(http: HttpClient) {
    super(http, "http://localhost:8080/musik/komponist/get", "komponist");
  }

  public override find(searchString: string = ''): Observable<Komponist[]> {
    const params = new HttpParams().set("komponist", searchString);
    return this.http.get<Komponist[]>(this.url, {params});
  }
}
