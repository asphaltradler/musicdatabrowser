import {Album} from '../app/entities/album';
import {Track} from '../app/entities/track';
import {Composer} from '../app/entities/composer';
import {Work} from '../app/entities/work';
import {Genre} from '../app/entities/genre';
import {Artist} from '../app/entities/artist';
import {AbstractEntity} from '../app/entities/abstractEntity';

export const paramEntity = 'entity';
export const paramSearchEntity = 'searchEntity';
export const paramSearchId = 'id';
export const paramSearchString = 'searchString';
export const detailsPath = 'details';

export const allEntities: typeof AbstractEntity[] = [Album, Track, Composer, Work, Genre, Artist];

export function getEntityForName(entityName: string): typeof AbstractEntity | undefined {
  return allEntities.find(e => e.entityName === entityName);
}
