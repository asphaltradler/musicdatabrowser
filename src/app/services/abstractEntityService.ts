import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';
import {appDefaults} from '../../config/config';

export abstract class AbstractEntityService<E extends AbstractEntity> {
  baseUrl = appDefaults.serverUrl;
  findUrl: string;
  getUrl: string;

  constructor(protected http: HttpClient,
              public entityType: typeof AbstractEntity) {
    this.findUrl = this.baseUrl + entityType.entityName + '/find';
    this.getUrl = this.baseUrl + entityType.entityName + '/get';
  }

  findByOtherNameLike(otherEntityName: string, searchString: string): Observable<E[]> {
    const params = new HttpParams().set(otherEntityName, searchString);
    return this.http.get<E[]>(this.findUrl, {params});
  }

  findNameLike(searchString: string): Observable<E[]> {
    return this.findByOtherNameLike(this.entityType.entityName, searchString);
  }

  findByOtherId(otherEntityName: string, id: number): Observable<E[]> {
    const params = new HttpParams().set(otherEntityName + 'Id', id);
    return this.http.get<E[]>(this.getUrl, {params});
  }
}
