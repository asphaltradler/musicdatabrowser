import {Routes} from '@angular/router';
import {AlbumListComponent} from './album-list/album-list.component';
import {KomponistListComponent} from './komponist-list/komponist-list.component';
import {StatusComponent} from './status/status.component';

export const routes: Routes = [
  { path: 'album', component: AlbumListComponent },
  { path: 'composer', component: KomponistListComponent },
  { path: 'status', component: StatusComponent }
];
