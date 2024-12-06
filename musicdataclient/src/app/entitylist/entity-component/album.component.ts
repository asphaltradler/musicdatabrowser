import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import {Album} from '../../entities/album';
import {NgForOf} from '@angular/common';
import {Composer} from '../../entities/composer';
import {Artist} from '../../entities/artist';
import {Genre} from '../../entities/genre';
import {Work} from '../../entities/work';
import {AbstractEntity} from '../../entities/abstractEntity';
import {EntityComponent} from './entity.component';
import {forkJoin} from "rxjs";

@Component({
  selector: 'tr.app-album-row',
  standalone: true,
  imports: [NgForOf],
  templateUrl: './album.component.html',
  styleUrls: ['../entity-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlbumComponent extends EntityComponent<Album> implements OnChanges {
  composers?: Composer[];
  artists?: Artist[];
  genres?: Genre[];
  works?: Work[];

  initialized = false;

  constructor(hostElement: ElementRef, private ref: ChangeDetectorRef) {
    super(hostElement);
  }

  lazyLoadLists() {
    if (!this.initialized) {
      console.log(`${this.entity.name} start lazy loading lists`);

      //alle zusammen füllen, wenn vorhanden, um zu viel Flackern zu vermeiden
      forkJoin({
        composers: this.getOtherEntitiesByThisId(Composer),
        works: this.getOtherEntitiesByThisId(Work),
        genres: this.getOtherEntitiesByThisId(Genre),
        artists: this.getOtherEntitiesByThisId(Artist)
      }).subscribe(data => {
        this.composers = data.composers.content;
        this.works = data.works.content;
        this.genres = data.genres.content;
        this.artists = data.artists.content;
        console.log(`=> ${this.entity.name} loaded:`, data);
        //erst jetzt für Änderung markieren, so dass View aktualisiert wird
        this.ref.markForCheck();
        this.initialized = true;
      });
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
    //gleiche Entities ignorieren
    if (newEntity !== prevEntity) {
      this.lazyLoadLists();
    } else {
      console.log(`change on ${newEntity} ignored`);
    }
  }
}
