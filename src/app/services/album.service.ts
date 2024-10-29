import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Album} from '../entities/album';

@Injectable({
  providedIn: 'root'
})
export class AlbumService {

  private url: string;

  constructor(private http: HttpClient) {
    this.url = 'http://localhost:8080/musik/album/get';
  }

  public find(searchString: string = ''): Observable<Album[]> {
    const params = new HttpParams().set("album", searchString);
    return this.http.get<Album[]>(this.url, {params});
  }
}
