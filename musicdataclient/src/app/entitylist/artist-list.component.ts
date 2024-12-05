import {Component} from '@angular/core';
import {EntityListComponent} from './entity-list.component';
import {Artist} from '../entities/artist';
import {ArtistService} from '../services/artist.service';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgComponentOutlet} from '@angular/common';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';
import {ComposerService} from '../services/composer.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';

@Component({
  selector: 'app-artist-list',
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
export class ArtistListComponent extends EntityListComponent<Artist> {
  constructor(service: ArtistService, route: ActivatedRoute, router: Router,
              composersService: ComposerService, artistsService: ArtistService,
              workService: WorkService, genreService: GenreService) {
    super(service, route, router, composersService, artistsService, workService, genreService);
  }
}
