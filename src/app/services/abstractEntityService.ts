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

  findByOtherNameLike(otherEntity: typeof AbstractEntity, searchString: string): Observable<E[]> {
    const params = new HttpParams().set(otherEntity.entityName, searchString);
    return this.http.get<E[]>(this.findUrl, {params});
  }

  findNameLike(searchString: string): Observable<E[]> {
    return this.findByOtherNameLike(this.entityType, searchString);
  }

  findByOtherId(otherEntityType: typeof AbstractEntity, id: number): Observable<E[]> {
    const params = new HttpParams().set(otherEntityType.entityName + 'Id', id);
    return this.http.get<E[]>(this.getUrl, {params});
  }
}
