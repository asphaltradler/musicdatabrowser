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
  findPageUrl = this.baseUrl + '/{}/find';
  findPageByUrl = this.baseUrl + '/{}/findby';
  getPageUrl = this.baseUrl + '/{}/get';
  getByIdUrl = this.baseUrl + '/{}/id/{id}';
  documentPageUrl = this.baseUrl + '/document/content/';

  constructor(private httpClient: HttpClient) {}

  public findByOtherNameLike<E extends AbstractEntity>(entityType: typeof AbstractEntity, otherEntity: typeof AbstractEntity, searchString: string, pageNumber?: number, pageSize?: number): Observable<Page<E>> {
    const params = new HttpParams()
      .set(otherEntity.entityName, searchString)
      .set(appDefaults.serviceParamPageNumber, pageNumber || 0)
      .set(appDefaults.serviceParamPageSize, pageSize || appDefaults.defaultPageSize);
    return this.getPage<E>(this.findPageByUrl, entityType, params);
  }

  public findNameLike<E extends AbstractEntity>(entityType: typeof AbstractEntity, searchString: string, pageNumber?: number, pageSize?: number): Observable<Page<E>> {
    const params = new HttpParams()
      .set(appDefaults.serviceParamName, searchString)
      .set(appDefaults.serviceParamPageNumber, pageNumber || 0)
      .set(appDefaults.serviceParamPageSize, pageSize || appDefaults.defaultPageSize);
    return this.getPage<E>(this.findPageUrl, entityType, params);
  }

  public findByOtherId<E extends AbstractEntity>(entityType: typeof AbstractEntity, otherEntityType: typeof AbstractEntity, otherEntityId: number, pageNumber?: number, pageSize?: number): Observable<Page<E>> {
    const params = new HttpParams()
      .set(otherEntityType.entityName + appDefaults.serviceParamSuffixId, otherEntityId)
      .set(appDefaults.serviceParamPageNumber, pageNumber || 0)
      .set(appDefaults.serviceParamPageSize, pageSize || appDefaults.defaultPageSize);
    return this.getPage<E>(this.getPageUrl, entityType, params);
  }

  private getPage<E extends AbstractEntity>(url: string, entityType: typeof AbstractEntity, params: HttpParams): Observable<Page<E>> {
    //console.log("getPage", url, params.get(appDefaults.serviceParamPageNumber));
    return this.httpClient.get<Page<E>>(url.replace('{}', entityType.entityName), {params});
  }

  public getById<E extends AbstractEntity>(entityType: typeof AbstractEntity, id: number): Observable<E> {
    //console.log("getPage", url, params.get(appDefaults.serviceParamPageNumber));
    return this.httpClient.get<E>(this.getByIdUrl.replace('{}', entityType.entityName)
      .replace('{id}', `${id}`));
  }

  public findDocumentById(id: number) {
    return this.httpClient.get<ImageBitmap>(this.getDocumentUrl(id));
  }

  public getDocumentUrl(id: number) {
    return this.documentPageUrl + id;
  }
}
