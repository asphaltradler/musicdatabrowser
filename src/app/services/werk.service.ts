import {Injectable} from '@angular/core';
import {AbstractEntityService} from './abstractEntityService';
import {Werk} from '../entities/werk';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class WerkService extends AbstractEntityService<Werk>{

  constructor(http: HttpClient) {
    super(http, 'werk');
  }
}
