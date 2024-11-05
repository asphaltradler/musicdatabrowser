import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
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
    FormsModule,
    ReactiveFormsModule,
    SearchfieldComponent
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent extends AbstractEntityList<Album> {
  constructor(service: AlbumService, route: ActivatedRoute) {
    super(service, route);
  }

  public override search(searchText: string) {
    const subscription = super.search(searchText);
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
    const entityName = queryParamMap.get(AlbumListComponent.urlParamEntityName) || undefined;
    const allowedNames = [ Komponist.name, Werk.name, Interpret.name ];
    for (const name of allowedNames) {
      const key = name.toLowerCase() + 'Id';
      if (queryParamMap.has(key)) {
        const id = queryParamMap.get(key) || undefined;
        console.log(`Suche ${this._entityNamePlural} nach ${key}=${id}`);
        const obs = this.service.findBy(key, <string>id);
        obs.subscribe(data => {
          this.extractData(data, entityName, id);
        });
        //nicht weitersuchen
        return;
      }
    }
    super.ngOnInit();
  }
}
