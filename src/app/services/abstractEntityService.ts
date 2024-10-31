import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';

export abstract class AbstractEntityService<E extends AbstractEntity> {
  baseUrl = 'http://localhost:8080/musik/';
  url: string;

  constructor(protected http: HttpClient,
              protected entityName: string) {
    this.url = this.baseUrl + entityName + '/get';
  }

  public find(searchString: string = ''): Observable<E[]> {
    const params = new HttpParams().set(this.entityName, searchString);
    return this.http.get<E[]>(this.url, {params});
  }
}
