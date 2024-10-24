import {Routes} from '@angular/router';
import {AlbumListComponent} from './album-list/album-list.component';
import {KomponistListComponent} from './komponist-list/komponist-list.component';
import {SearchAlbumComponent} from './search-album/search-album.component';

export const routes: Routes = [
  { path: 'album', component: AlbumListComponent },
  { path: 'composer', component: KomponistListComponent },
  { path: 'search', component: SearchAlbumComponent }
];
