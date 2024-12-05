import {Component} from '@angular/core';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {Genre} from '../entities/genre';
import {GenreService} from '../services/genre.service';
import {NgComponentOutlet} from '@angular/common';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';
import {ComposerService} from '../services/composer.service';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';

@Component({
  selector: 'app-genre-list',
  standalone: true,
  imports: [
    NgComponentOutlet,
    SearchfieldComponent,
    PagingComponent,
    ListHeaderComponent
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class GenreListComponent extends EntityListComponent<Genre> {
  constructor(route: ActivatedRoute, router: Router,
              composersService: ComposerService, artistsService: ArtistService,
              workService: WorkService, genreService: GenreService) {
    super(genreService, route, router, composersService, artistsService, workService, genreService);
  }
}
