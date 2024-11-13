import {Component} from '@angular/core';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';
import {ActivatedRoute, Router} from '@angular/router';
import {Genre} from '../entities/genre';
import {GenreService} from '../services/genre.service';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-genre-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class GenreListComponent extends AbstractEntityList<Genre> {
  constructor(genreService: GenreService, route: ActivatedRoute, router: Router) {
    super(genreService, route, router);
  }

}
