import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';
import {ActivatedRoute, Router} from '@angular/router';
import {appDefaults} from '../app.config';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    SearchfieldComponent
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent extends AbstractEntityList<Album> {

  constructor(service: AlbumService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
  }

  public override searchForName(searchText: string) {
    const subscription = super.searchForName(searchText);
    subscription.add(() => {
      console.log('Create track URLs for data');
      for (const alb of this.entities) {
        alb.track_url = appDefaults.serverUrl + 'track/get?albumId=' + alb.id;
      }
    });
    return subscription;
  }

}
