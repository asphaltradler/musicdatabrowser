import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';

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
export class AlbumListComponent extends AbstractEntityList<Album> {
  constructor(service: AlbumService) {
    super(service);
    this.namePlural = 'Alben';
  }

  public override search(searchText: string) {
    const subscription = super.search(searchText);
    subscription.add(() => {
      console.log('Create URLs for data');
      for (const alb of this.entities) {
        alb.track_url = 'http://localhost:8080/musik/track/get?album-id=' + alb.id;
      }
    });
    return subscription;
  }
}
