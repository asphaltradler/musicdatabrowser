import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';
import {appDefaults} from '../../config/config';
import {Page} from '../entities/page';
import {Injectable} from '@angular/core';

@Injectable(
  { providedIn: 'root' }
)
/**
 * Service for performing CRUD and search operations on entities via HTTP requests.
 *
 * Provides generic methods to interact with different types of entities that extend `AbstractEntity`.
 * Supports searching by name, related entity name or ID, retrieving by ID, removing by ID,
 * and fetching associated documents or thumbnails.
 *
 * @template E Type of the entity extending `AbstractEntity`.
 *
 * @example
 * // Find entities by name
 * entityService.findNameLike(MyEntity, 'searchTerm').subscribe(...);
 *
 * @example
 * // Get entity by ID
 * entityService.getById(MyEntity, 123).subscribe(...);
 *
 * @example
 * // Remove entity by ID
 * entityService.removeById(MyEntity, 123).subscribe(...);
 */
export class EntityService {
  baseUrl = appDefaults.serverUrl;
  findPageUrl = this.baseUrl + '/{}/find';
  findPageByUrl = this.baseUrl + '/{}/findby';
  getPageUrl = this.baseUrl + '/{}/get';
  getByIdUrl = this.baseUrl + '/{}/id/{id}';
  removeByIdUrl = this.baseUrl + '/{}/remove/{id}';
  documentUrl = this.baseUrl + '/document/content/';
  thumbnailUrl = this.baseUrl + '/document/thumb/';

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
    return this.httpClient.get<E>(this.getByIdUrl.replace('{}', entityType.entityName)
      .replace('{id}', `${id}`));
  }

  public removeById(entityType: typeof AbstractEntity, id: number) {
    console.log("removeById", id);
    return this.httpClient.delete(this.removeByIdUrl.replace('{}', entityType.entityName)
      .replace('{id}', `${id}`));
  }

  public findDocumentById(id: number) {
    return this.httpClient.get<ImageBitmap>(this.getDocumentUrl(id));
  }

  public getDocumentUrl(id: number) {
    return this.documentUrl + id;
  }

  public getThumbnailUrl(id: number) {
    return this.thumbnailUrl + id;
  }
}
