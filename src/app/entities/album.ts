import {AbstractEntity} from './abstractEntity';
import {Interpret} from './interpret';
import {Komponist} from './komponist';
import {Genre} from './genre';
import {Werk} from './werk';

export class Album extends AbstractEntity {
  static override entityName = 'album';
  static override namePlural = 'Alben';

  //tracks?: Track[];
  tracks?: number[];
  komponisten?: Komponist[];
  interpreten?: Interpret[];
  genres?: Genre[];
  werke?: Werk[];
}
