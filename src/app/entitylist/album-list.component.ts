import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';
import {ActivatedRoute} from '@angular/router';
import {Komponist} from '../entities/komponist';
import {Interpret} from '../entities/interpret';
import {Werk} from '../entities/werk';

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
  public static searchEntityNames = [ Komponist.name, Werk.name, Interpret.name ];

  constructor(service: AlbumService, route: ActivatedRoute) {
    super(service, route);
  }

  public override searchForName(searchText: string) {
    const subscription = super.searchForName(searchText);
    subscription.add(() => {
      console.log('Create track URLs for data');
      for (const alb of this.entities) {
        alb.track_url = 'http://localhost:8080/musik/track/get?albumId=' + alb.id;
      }
    });
    return subscription;
  }

  public override ngOnInit() {
    const queryParamMap = this.route.snapshot.queryParamMap;
    for (const name of AlbumListComponent.searchEntityNames) {
      const key = name.toLowerCase() + 'Id';
      if (queryParamMap.has(key)) {
        this.searchForId(key);
        //nicht weitersuchen
        return;
      }
    }
    super.ngOnInit();
  }
}
