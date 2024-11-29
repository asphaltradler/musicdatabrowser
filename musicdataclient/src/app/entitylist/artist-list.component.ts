import {Component} from '@angular/core';
import {EntityListComponent} from './entity-list.component';
import {Artist} from '../entities/artist';
import {ArtistService} from '../services/artist.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-artist-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class ArtistListComponent extends EntityListComponent<Artist> {
  constructor(artistService: ArtistService, route: ActivatedRoute, router: Router) {
    super(artistService, route, router);
  }
}
