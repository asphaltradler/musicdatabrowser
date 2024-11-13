import {Component} from '@angular/core';
import {SearchfieldComponent} from '../search/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {TrackService} from '../services/track.service';
import {AbstractEntityList} from './abstractEntityList';
import {Track} from '../entities/track';
import {Album} from '../entities/album';
import {Komponist} from '../entities/komponist';
import {Interpret} from '../entities/interpret';
import {Werk} from '../entities/werk';
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
  styleUrl: './track-list.component.css'
})
export class TrackListComponent extends AbstractEntityList<Track> {
  constructor(service: TrackService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
  }

  protected readonly Album = Album;
  protected readonly Komponist = Komponist;
  protected readonly Interpret = Interpret;
  protected readonly Werk = Werk;
  protected readonly Genre = Genre;
  protected readonly Track = Track;

  trackByItemId(index: number, item: Track): number {
    return item.id;
  }
}
