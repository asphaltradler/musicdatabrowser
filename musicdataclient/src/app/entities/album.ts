import {AbstractEntity} from './abstractEntity';
import {Artist} from './artist';
import {Composer} from './composer';
import {Genre} from './genre';
import {Work} from './work';

export class Album extends AbstractEntity {
  static override entityName = 'album';
  static override namePlural = 'Alben';

  tracks?: number[];
  composers?: Composer[];
  artists?: Artist[];
  genres?: Genre[];
  works?: Work[];
}
