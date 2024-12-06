import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';
import {appDefaults} from '../../config/config';
import {Page} from '../entities/page';
import {Injectable} from '@angular/core';

@Injectable(
  { providedIn: 'root' }
)
export class EntityService {
  baseUrl = appDefaults.serverUrl;
  findUrl = this.baseUrl + '{}' + '/find';
  findByUrl = this.baseUrl + '{}' + '/findby';
  getUrl = this.baseUrl + '{}' + '/get';

  constructor(private httpClient: HttpClient) {}

  findByOtherNameLike(entityType: typeof AbstractEntity, otherEntity: typeof AbstractEntity, searchString: string, pageNumber: number, pageSize: number): Observable<Page<any>> {
    const params = new HttpParams()
      .set(otherEntity.entityName, searchString)
      .set(appDefaults.serviceParamPageNumber, pageNumber)
      .set(appDefaults.serviceParamPageSize, pageSize);
    return this.getPage(this.findByUrl, entityType, params);
  }

  findNameLike(entityType: typeof AbstractEntity, searchString: string, pageNumber: number, pageSize: number): Observable<Page<any>> {
    const params = new HttpParams()
      .set(appDefaults.serviceParamName, searchString)
      .set(appDefaults.serviceParamPageNumber, pageNumber)
      .set(appDefaults.serviceParamPageSize, pageSize);
    return this.getPage(this.findUrl, entityType, params);
  }

  findByOtherId(entityType: typeof AbstractEntity, otherEntityType: typeof AbstractEntity, otherEntityId: number, pageNumber: number, pageSize: number): Observable<Page<any>> {
    const params = new HttpParams()
      .set(otherEntityType.entityName + appDefaults.serviceParamSuffixId, otherEntityId)
      .set(appDefaults.serviceParamPageNumber, pageNumber)
      .set(appDefaults.serviceParamPageSize, pageSize);
    return this.getPage(this.getUrl, entityType, params);
  }

  protected getPage(url: string, entityType: typeof AbstractEntity, params: HttpParams): Observable<Page<any>> {
    //console.log("getPage", url, params.get(appDefaults.serviceParamPageNumber));
    return this.httpClient.get<Page<any>>(url.replace('{}', entityType.entityName), {params});
  }
}
