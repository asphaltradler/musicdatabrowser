import {Injectable} from '@angular/core';
import {AbstractEntityService} from './abstractEntityService';
import {HttpClient} from '@angular/common/http';
import {Interpret} from '../entities/interpret';

@Injectable({
  providedIn: 'root'
})
export class InterpretService extends AbstractEntityService<Interpret> {
  constructor(http: HttpClient) {
    super(http, Interpret);
  }
}
