import {Album} from '../app/entities/album';
import {Track} from '../app/entities/track';
import {Composer} from '../app/entities/composer';
import {Work} from '../app/entities/work';
import {Genre} from '../app/entities/genre';
import {Artist} from '../app/entities/artist';
import {AbstractEntity} from '../app/entities/abstractEntity';

export const paramEntity = 'entity';
export const paramEntityType = 'entityType';
export const paramSearchEntity = 'searchEntity';
export const paramSourceEntity = 'sourceEntity';
export const paramId = 'id';
export const paramSearchText = 'searchText';
export const paramFilterText = 'filterText';
export const paramPageNumber = 'pageNumber';
export const paramPageSize = 'pageSize';
export const detailsPath = 'details';

export const allEntities: typeof AbstractEntity[] = [Album, Track, Composer, Work, Genre, Artist];

export function getEntityForName(entityName: string): typeof AbstractEntity | undefined {
  return allEntities.find(e => e.entityName === entityName);
}
