import {Component, OnChanges, SimpleChanges} from '@angular/core';
import {Album} from '../../entities/album';
import {NgForOf} from '@angular/common';
import {Composer} from '../../entities/composer';
import {Artist} from '../../entities/artist';
import {Genre} from '../../entities/genre';
import {Work} from '../../entities/work';
import {ComposerService} from '../../services/composer.service';
import {ArtistService} from '../../services/artist.service';
import {WorkService} from '../../services/work.service';
import {GenreService} from '../../services/genre.service';
import {appDefaults} from '../../../config/config';
import {AbstractEntity} from '../../entities/abstractEntity';
import {EntityComponent} from './entity.component';

@Component({
  selector: 'tr.app-album-row',
  standalone: true,
  imports: [NgForOf],
  templateUrl: './album.component.html',
  styleUrls: ['../entity-list.component.css'],
  //changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlbumComponent extends EntityComponent<Album> implements OnChanges {
  composers?: Composer[];
  artists?: Artist[];
  genres?: Genre[];
  works?: Work[];

  initialized = false;

  lazyLoad(composersService: ComposerService, artistsService: ArtistService,
           workService: WorkService, genreService: GenreService) {
    if (!this.initialized) {
      console.log('lazy loading lists for', this.entity.name);
      composersService.findByOtherId(Album, this.entity.id, 0, appDefaults.maxPageSizeForLists).subscribe(data => {
        this.composers = data.content;
      });
      artistsService.findByOtherId(Album, this.entity.id, 0, appDefaults.maxPageSizeForLists).subscribe(data => {
        this.artists = data.content;
      });
      workService.findByOtherId(Album, this.entity.id, 0, appDefaults.maxPageSizeForLists).subscribe(data => {
        this.works = data.content;
      });
      genreService.findByOtherId(Album, this.entity.id, 0, appDefaults.maxPageSizeForLists).subscribe(data => {
        this.genres = data.content;
      });
      /*
      trackService.findBy(Album, this.entity.id).subscribe(data => {
        this.entity.tracks = data;
      });
      */
      this.initialized = true;
      //TODO push change
    }
  }

  getSearchEntities(entity: typeof AbstractEntity): AbstractEntity[] | undefined {
    if (entity === Composer) {
      return this.composers;
    } else if (entity === Artist) {
      return this.artists;
    } else if (entity === Work) {
      return this.works;
    } else if (entity === Genre) {
      return this.genres;
    }
    return [];
  }

  ngOnChanges(changes:SimpleChanges) {
    const modelChange = changes['entity'];
    const prevEntity: Album = modelChange?.previousValue;
    const newEntity: Album = modelChange?.currentValue;
    console.log('change on', newEntity);
    //gleiche Entities ignorieren
    if (newEntity !== prevEntity) {
      this.lazyLoad(
        this.entityList.composersService, this.entityList.artistsService,
        this.entityList.workService, this.entityList.genreService);
    }
  }
}
