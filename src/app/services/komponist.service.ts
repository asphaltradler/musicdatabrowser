import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Komponist} from '../entities/komponist';
import {AbstractEntityService} from './abstractEntityService';

@Injectable({
  providedIn: 'root'
})
export class KomponistService extends AbstractEntityService<Komponist> {
  constructor(http: HttpClient) {
    super(http, Komponist.name);
  }
}
