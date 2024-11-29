import {Injectable} from '@angular/core';
import {AbstractEntityService} from './abstractEntityService';
import {Work} from '../entities/work';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class WorkService extends AbstractEntityService<Work>{
  constructor(http: HttpClient) {
    super(http, Work);
  }
}
