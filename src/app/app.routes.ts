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

export const routes: Routes = [
  { path: Album.name, component: AlbumListComponent },
  { path: Komponist.name, component: KomponistListComponent },
  { path: Werk.name, component: WerkListComponent },
  { path: Interpret.name, component: InterpretListComponent },
  { path: 'status', component: StatusComponent }
];
