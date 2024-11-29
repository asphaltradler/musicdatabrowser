import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Composer} from '../entities/composer';
import {AbstractEntityService} from './abstractEntityService';

@Injectable({
  providedIn: 'root'
})
export class ComposerService extends AbstractEntityService<Composer> {
  constructor(http: HttpClient) {
    super(http, Composer);
  }
}
