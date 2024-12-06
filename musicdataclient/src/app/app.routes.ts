import {Routes} from '@angular/router';
import {StatusComponent} from './status/status.component';
import {Album} from './entities/album';
import {Composer} from './entities/composer';
import {Work} from './entities/work';
import {Artist} from './entities/artist';
import {Genre} from './entities/genre';
import {Track} from './entities/track';
import {EntityListComponent} from './entitylist/entity-list.component';

export const routes: Routes = [
  { path: Album.entityName, component: EntityListComponent<Album>, title: Album.namePlural, data: [Album] },
  { path: Track.entityName, component: EntityListComponent<Track>, title: Track.namePlural, data: [Track] },
  { path: Composer.entityName, component: EntityListComponent<Composer>, title: Composer.namePlural, data: [Composer] },
  { path: Work.entityName, component: EntityListComponent<Work>, title: Work.namePlural, data: [Work] },
  { path: Genre.entityName, component: EntityListComponent<Genre>, title: Genre.namePlural, data: [Genre] },
  { path: Artist.entityName, component: EntityListComponent<Artist>, title: Artist.namePlural, data: [Artist] },
  { path: 'status', component: StatusComponent, title: 'Status' },
  { path: '', redirectTo: Album.entityName, pathMatch: 'full'}
];
