import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';
import {Track} from '../entities/track';
import {KomponistService} from '../services/komponist.service';
import {Komponist} from '../entities/komponist';
import {InterpretService} from '../services/interpret.service';
import {WerkService} from '../services/werk.service';
import {GenreService} from '../services/genre.service';
import {AbstractEntity} from '../entities/abstractEntity';
import {Interpret} from '../entities/interpret';
import {Werk} from '../entities/werk';
import {Genre} from '../entities/genre';

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
  constructor(service: AlbumService, route: ActivatedRoute, router: Router,
              private komponistenService: KomponistService, private interpretenService: InterpretService,
              private werkService: WerkService, private genreService: GenreService) {
    super(service, route, router);
  }

  override fillEntities(data: Album[], titleName?: string) {
    super.fillEntities(data, titleName);
    this._entities.forEach((album: Album) => {
      this.komponistenService.findBy(Album.entityName, album.id.toString()).subscribe(data => {
        album.komponisten = data;
      });
      this.interpretenService.findBy(Album.entityName, album.id.toString()).subscribe(data => {
        album.interpreten = data;
      });
      this.werkService.findBy(Album.entityName, album.id.toString()).subscribe(data => {
        album.werke = data;
      });
      this.genreService.findBy(Album.entityName, album.id.toString()).subscribe(data => {
        album.genres = data;
      });
      /*
      this.trackService.findBy(Album.entityName, album.id.toString()).subscribe(data => {
        album.tracks = data;
      });
       */
    })
  }

  getMultiEntity(album: Album, entity: typeof AbstractEntity): AbstractEntity[] {
    if (entity === Komponist) {
      return album.komponisten || [];
    } else if (entity === Interpret) {
      return album.interpreten || [];
    } else if (entity === Werk) {
      return album.werke || [];
    } else if (entity === Genre) {
      return album.genres || [];
    }
    return [];
  }

  protected readonly Track = Track;
  protected readonly Komponist = Komponist;
  protected readonly Album = Album;
}
