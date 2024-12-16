import {AbstractEntity} from './abstractEntity';

export class Album extends AbstractEntity {
  static override entityName = 'album';
  static override namePlural = 'Alben';

  bookletId?: Number;
  bookletName?: string;
}
