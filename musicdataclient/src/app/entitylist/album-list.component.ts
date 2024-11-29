import {Component} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';
import {Track} from '../entities/track';
import {ComposerService} from '../services/composer.service';
import {Composer} from '../entities/composer';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';
import {AbstractEntity} from '../entities/abstractEntity';
import {Artist} from '../entities/artist';
import {Work} from '../entities/work';
import {Genre} from '../entities/genre';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './album-list.component.html',
  styleUrls: ['./entity-list.component.css', './album-list.component.css']
})
export class AlbumListComponent extends EntityListComponent<Album> {
  constructor(service: AlbumService, route: ActivatedRoute, router: Router,
              private composersService: ComposerService, private artistService: ArtistService,
              private workService: WorkService, private genreService: GenreService) {
    super(service, route, router);
  }

  override fillData(data: Album[]) {
    super.fillData(data);
    this._entities.forEach((album: Album) => {
      this.composersService.findByOtherId(Album, album.id).subscribe(data => {
        album.composers = data;
      });
      this.artistService.findByOtherId(Album, album.id).subscribe(data => {
        album.artists = data;
      });
      this.workService.findByOtherId(Album, album.id).subscribe(data => {
        album.works = data;
      });
      this.genreService.findByOtherId(Album, album.id).subscribe(data => {
        album.genres = data;
      });
      /*
      this.trackService.findBy(Album, album.id).subscribe(data => {
        album.tracks = data;
      });
       */
    })
  }

  getEntityList(album: Album, entity: typeof AbstractEntity): AbstractEntity[] {
    if (entity === Composer) {
      return album.composers || [];
    } else if (entity === Artist) {
      return album.artists || [];
    } else if (entity === Work) {
      return album.works || [];
    } else if (entity === Genre) {
      return album.genres || [];
    }
    return [];
  }

  protected readonly Track = Track;
  protected readonly Album = Album;
}
