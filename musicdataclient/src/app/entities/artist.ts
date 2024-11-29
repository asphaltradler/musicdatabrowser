import {AbstractEntity} from './abstractEntity';

export class Artist extends AbstractEntity {
  static override entityName = 'artist';
  static override namePlural = 'Interpreten';
}
