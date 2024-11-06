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
  { path: Album.name, component: AlbumListComponent, title: 'Liste der Alben' },
  { path: Komponist.name, component: KomponistListComponent, title: 'Liste der Komponisten' },
  { path: Werk.name, component: WerkListComponent, title: 'Liste der Werke' },
  { path: Interpret.name, component: InterpretListComponent, title: 'Liste der Interpreten' },
  { path: 'status', component: StatusComponent, title: 'Statusanzeige' },
  { path: '', redirectTo: Album.name, pathMatch: 'full'}
];
