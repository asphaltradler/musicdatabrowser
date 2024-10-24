import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Komponist} from '../entities/komponist';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class KomponistService {

  private url: string;

  constructor(private http: HttpClient) {
    this.url = 'http://localhost:8080/musik/komponist/get';
  }

  public find(searchString: string = ''): Observable<Komponist[]> {
    const params = new HttpParams().set("komponist", searchString);
    return this.http.get<Komponist[]>(this.url, {params});
  }
}
