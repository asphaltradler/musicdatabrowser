import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractEntity} from '../entities/abstractEntity';

export abstract class AbstractEntityService<E extends AbstractEntity> {
  constructor(protected http: HttpClient,
              protected url: string,
              protected searchPath: string) {
  }

  public find(searchString: string = ''): Observable<E[]> {
    const params = new HttpParams().set(this.searchPath, searchString);
    return this.http.get<E[]>(this.url, {params});
  }
}
