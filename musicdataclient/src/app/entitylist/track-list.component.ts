import {Component} from '@angular/core';
import {SearchfieldComponent} from '../search/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {TrackService} from '../services/track.service';
import {EntityListComponent} from './entity-list.component';
import {Track} from '../entities/track';
import {Album} from '../entities/album';
import {Composer} from '../entities/composer';
import {Artist} from '../entities/artist';
import {Work} from '../entities/work';
import {Genre} from '../entities/genre';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-track-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
    NgIf,
  ],
  templateUrl: './track-list.component.html',
  styleUrls: ['./entity-list.component.css', './track-list.component.css']
})
export class TrackListComponent extends EntityListComponent<Track> {
  constructor(service: TrackService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
  }

  protected readonly Album = Album;
  protected readonly Composer = Composer;
  protected readonly Artist = Artist;
  protected readonly Work = Work;
  protected readonly Genre = Genre;
  protected readonly Track = Track;
}
