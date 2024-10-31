import {Routes} from '@angular/router';
import {AlbumListComponent} from './entitylist/album-list.component';
import {KomponistListComponent} from './entitylist/komponist-list.component';
import {StatusComponent} from './status/status.component';
import {InterpretListComponent} from './entitylist/interpret-list.component';

export const routes: Routes = [
  { path: 'album', component: AlbumListComponent },
  { path: 'composer', component: KomponistListComponent },
  { path: 'interpret', component: InterpretListComponent },
  { path: 'status', component: StatusComponent }
];
