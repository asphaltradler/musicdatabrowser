import {AbstractEntity} from './abstractEntity';

export class Genre extends AbstractEntity {
  static override entityName = 'genre';
  static override namePlural = 'Genres';
}
