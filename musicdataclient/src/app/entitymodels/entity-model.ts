import {AbstractEntity} from '../entities/abstractEntity';
import {ComposerService} from '../services/composer.service';
import {ArtistService} from '../services/artist.service';
import {WorkService} from '../services/work.service';
import {GenreService} from '../services/genre.service';

export class EntityModel<ENTITY extends AbstractEntity> {

  protected initialized = false;

  constructor(public entity: ENTITY) {
  }

  lazyLoad(composersService: ComposerService, artistsService: ArtistService,
           workService: WorkService, genreService: GenreService) {
    this.initialized = true;
  }

  getSearchEntities(entity: typeof AbstractEntity): AbstractEntity[] | undefined {
    return [];
  }
}
