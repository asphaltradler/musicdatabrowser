import {Component} from '@angular/core';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {TrackService} from '../services/track.service';
import {EntityListComponent} from './entity-list.component';
import {Track} from '../entities/track';
import {Album} from '../entities/album';
import {Composer} from '../entities/composer';
import {Artist} from '../entities/artist';
import {Work} from '../entities/work';
import {Genre} from '../entities/genre';
import {NgComponentOutlet} from '@angular/common';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';
import {ComposerService} from '../services/composer.service';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';

@Component({
  selector: 'app-track-list',
  standalone: true,
  imports: [
    NgComponentOutlet,
    SearchfieldComponent,
    PagingComponent,
    ListHeaderComponent,

  ],
  templateUrl: './entity-list.component.html',
  styleUrls: ['./entity-list.component.css', './track-list.component.css']
})
export class TrackListComponent extends EntityListComponent<Track> {
  constructor(service: TrackService, route: ActivatedRoute, router: Router,
              composersService: ComposerService, artistsService: ArtistService,
              workService: WorkService, genreService: GenreService) {
    super(service, route, router, composersService, artistsService, workService, genreService);
  }

  protected readonly Album = Album;
  protected readonly Composer = Composer;
  protected readonly Artist = Artist;
  protected readonly Work = Work;
  protected readonly Genre = Genre;
  protected readonly Track = Track;
}
