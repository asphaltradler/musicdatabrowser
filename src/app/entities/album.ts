import {AbstractEntity} from './abstractEntity';

export class Album extends AbstractEntity {
  track_url: string | undefined;
  static override entityName = 'album';
  static override namePlural = 'Alben';
}
