import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';
import {ComposerService} from '../services/composer.service';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';
import {AlbumComponent} from './entity-component/album.component';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    NgForOf,
    SearchfieldComponent,
    PagingComponent,
    ListHeaderComponent,
    AlbumComponent,
  ],
  templateUrl: './album-list.component.html',
  styleUrls: ['./entity-list.component.css', './album-list.component.css']
})
export class AlbumListComponent extends EntityListComponent<Album> {
  constructor(service: AlbumService, route: ActivatedRoute, router: Router,
              composersService: ComposerService, artistsService: ArtistService,
              workService: WorkService, genreService: GenreService) {
    super(service, route, router, composersService, artistsService, workService, genreService);
  }
}
