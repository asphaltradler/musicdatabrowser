import {Routes} from '@angular/router';
import {AlbumListComponent} from './entitylist/album-list.component';
import {KomponistListComponent} from './entitylist/komponist-list.component';
import {StatusComponent} from './status/status.component';
import {InterpretListComponent} from './entitylist/interpret-list.component';
import {WerkListComponent} from './entitylist/werk-list.component';
import {Album} from './entities/album';
import {Komponist} from './entities/komponist';
import {Werk} from './entities/werk';
import {Interpret} from './entities/interpret';
import {GenreListComponent} from './entitylist/genre-list.component';
import {Genre} from './entities/genre';
import {Track} from './entities/track';
import {TrackListComponent} from './entitylist/track-list.component';

export const routes: Routes = [
  { path: Album.name, component: AlbumListComponent, title: Album.namePlural },
  { path: Track.name, component: TrackListComponent, title: Track.namePlural },
  { path: Komponist.name, component: KomponistListComponent, title: Komponist.namePlural },
  { path: Werk.name, component: WerkListComponent, title: Werk.namePlural },
  { path: Genre.name, component: GenreListComponent, title: Genre.namePlural },
  { path: Interpret.name, component: InterpretListComponent, title: Interpret.namePlural },
  { path: 'status', component: StatusComponent, title: 'Status' },
  { path: '', redirectTo: Album.name, pathMatch: 'full'}
];
