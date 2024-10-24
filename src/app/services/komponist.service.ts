import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
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

  public findAll(): Observable<Komponist[]> {
    return this.http.get<Komponist[]>(this.url);
  }
}
