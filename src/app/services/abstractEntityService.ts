import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';
import {appDefaults} from '../../config/config';

export abstract class AbstractEntityService<E extends AbstractEntity> {
  baseUrl = appDefaults.serverUrl;
  findUrl: string;
  getUrl: string;

  constructor(protected http: HttpClient,
              public entityName: string, public entityNamePlural: string) {
    this.findUrl = this.baseUrl + entityName.toLowerCase() + '/find';
    this.getUrl = this.baseUrl + entityName.toLowerCase() + '/get';
  }

  public find(searchString: string = ''): Observable<E[]> {
    const params = new HttpParams().set(this.entityName.toLowerCase(), searchString);
    return this.http.get<E[]>(this.findUrl, {params});
  }

  public findBy(otherEntityName: string, id: string): Observable<E[]> {
    const params = new HttpParams().set(otherEntityName, id);
    return this.http.get<E[]>(this.getUrl, {params});
  }
}
