import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';
import {Track} from '../entities/track';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent extends AbstractEntityList<Album> {
  constructor(service: AlbumService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
    //Track behandeln wir extra
    this._searchableEntities = this._searchableEntities.filter(
      (entity) => entity != Track
    );
  }

  protected readonly Track = Track;
}
