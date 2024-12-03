import {Album} from '../entities/album';
import {Composer} from '../entities/composer';
import {Artist} from '../entities/artist';
import {Genre} from '../entities/genre';
import {Work} from '../entities/work';
import {ComposerService} from '../services/composer.service';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';
import {appDefaults} from '../../config/config';
import {EntityModel} from './entity-model';
import {AbstractEntity} from '../entities/abstractEntity';

export class AlbumModel extends EntityModel<Album>{
  composers?: Composer[];
  artists?: Artist[];
  genres?: Genre[];
  works?: Work[];

  override lazyLoad(composersService: ComposerService, artistsService: ArtistService,
                    workService: WorkService, genreService: GenreService) {
    if (!this.initialized) {
      console.log('lazy loading lists for', this.entity);
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
      trackService.findBy(Album, this.album.id).subscribe(data => {
        this.album.tracks = data;
      });
      */
      this.initialized = true;
    }
  }

  override getSearchEntities(entity: typeof AbstractEntity): AbstractEntity[] | undefined {
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
}
