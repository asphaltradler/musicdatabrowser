import {Component} from '@angular/core';
import {Composer} from '../entities/composer';
import {ComposerService} from '../services/composer.service';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgComponentOutlet} from '@angular/common';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';

@Component({
  selector: 'app-composer-list',
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
export class ComposerListComponent extends EntityListComponent<Composer> {
  constructor(route: ActivatedRoute, router: Router,
              composersService: ComposerService, artistsService: ArtistService,
              workService: WorkService, genreService: GenreService) {
    super(composersService, route, router, composersService, artistsService, workService, genreService);
  }
}
