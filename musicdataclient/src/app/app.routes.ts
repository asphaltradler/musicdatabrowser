import {Routes} from '@angular/router';
import {AlbumListComponent} from './entitylist/album-list.component';
import {ComposerListComponent} from './entitylist/composer-list.component';
import {StatusComponent} from './status/status.component';
import {ArtistListComponent} from './entitylist/artist-list.component';
import {WorkListComponent} from './entitylist/work-list.component';
import {Album} from './entities/album';
import {Composer} from './entities/composer';
import {Work} from './entities/work';
import {Artist} from './entities/artist';
import {Genre} from './entities/genre';
import {Track} from './entities/track';
import {TrackListComponent} from './entitylist/track-list.component';
import {GenreListComponent} from './entitylist/genre-list.component';

export const routes: Routes = [
  { path: Album.entityName, component: AlbumListComponent, title: Album.namePlural },
  { path: Track.entityName, component: TrackListComponent, title: Track.namePlural },
  { path: Composer.entityName, component: ComposerListComponent, title: Composer.namePlural },
  { path: Work.entityName, component: WorkListComponent, title: Work.namePlural },
  { path: Genre.entityName, component: GenreListComponent, title: Genre.namePlural },
  { path: Artist.entityName, component: ArtistListComponent, title: Artist.namePlural },
  { path: 'status', component: StatusComponent, title: 'Status' },
  { path: '', redirectTo: Album.entityName, pathMatch: 'full'}
];
