import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';
import {appDefaults} from '../../config/config';
import {Page} from '../entities/page';

export abstract class AbstractEntityService<E extends AbstractEntity> {
  baseUrl = appDefaults.serverUrl;
  findUrl: string;
  findByUrl: string;
  getUrl: string;

  protected constructor(protected http: HttpClient,
                        public entityType: typeof AbstractEntity) {
    this.findUrl = this.baseUrl + entityType.entityName + '/find';
    this.findByUrl = this.baseUrl + entityType.entityName + '/findby';
    this.getUrl = this.baseUrl + entityType.entityName + '/get';
  }

  findByOtherNameLike(otherEntity: typeof AbstractEntity, searchString: string, pageNumber: number, pageSize: number): Observable<Page<E>> {
    const params = new HttpParams()
      .set(otherEntity.entityName, searchString)
      .set(appDefaults.serviceParamPageNumber, pageNumber)
      .set(appDefaults.serviceParamPageSize, pageSize);
    return this.http.get<Page<E>>(this.findByUrl, {params});
  }

  findNameLike(searchString: string, pageNumber: number, pageSize: number): Observable<Page<E>> {
    const params = new HttpParams()
      .set(appDefaults.serviceParamName, searchString)
      .set(appDefaults.serviceParamPageNumber, pageNumber)
      .set(appDefaults.serviceParamPageSize, pageSize);
    return this.http.get<Page<E>>(this.findUrl, {params});
  }

  findByOtherId(otherEntityType: typeof AbstractEntity, id: number, pageNumber: number, pageSize: number): Observable<Page<E>> {
    const params = new HttpParams()
      .set(otherEntityType.entityName + appDefaults.serviceParamSuffixId, id)
      .set(appDefaults.serviceParamPageNumber, pageNumber)
      .set(appDefaults.serviceParamPageSize, pageSize);
    return this.http.get<Page<E>>(this.getUrl, {params});
  }
}
