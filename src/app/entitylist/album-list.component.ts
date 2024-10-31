import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchfieldComponent} from '../search/searchfield.component';
import {EntityListComponent} from './abstractEntityList';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SearchfieldComponent
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent extends EntityListComponent<Album> {
  constructor(albumService: AlbumService) {
    super(albumService);
    this.name = Album.name;
    this.namePlural = 'Alben';
  }

  public override search(searchText: string): Observable<Album[]> {
    const obs = super.search(searchText);
    obs.subscribe(() => {
      console.log('Create URLs for data');
      for (const alb of this.entities) {
        alb.track_url = 'http://localhost:8080/musik/track/get?album-id=' + alb.id;
      }
    });
    return obs;
  }
}
