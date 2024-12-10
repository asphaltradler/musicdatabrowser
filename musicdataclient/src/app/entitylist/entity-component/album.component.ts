import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef} from '@angular/core';
import {Album} from '../../entities/album';
import {NgForOf} from '@angular/common';
import {Composer} from '../../entities/composer';
import {Artist} from '../../entities/artist';
import {Genre} from '../../entities/genre';
import {Work} from '../../entities/work';
import {AbstractEntity} from '../../entities/abstractEntity';
import {EntityComponent} from './entity.component';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'tr.app-album-row',
  standalone: true,
  imports: [NgForOf],
  templateUrl: './album.component.html',
  styleUrls: ['../entity-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlbumComponent extends EntityComponent<Album> {
  composers?: Composer[];
  artists?: Artist[];
  genres?: Genre[];
  works?: Work[];
  obs: IntersectionObserver;

  constructor(hostElement: ElementRef, private changeRef: ChangeDetectorRef) {
    super(hostElement);

    this.obs = new IntersectionObserver(entries => entries.filter(
        e => e.isIntersecting).forEach(() => this.lazyLoadLists()));
    this.obs.observe(this.hostElement.nativeElement);
  }

  lazyLoadLists() {
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
      this.changeRef.markForCheck();
    });
    //keine weitere Observierung mehr nötig nach Initiierung des Ladens
    this.obs.disconnect();
  }

  getAlbumartUrl() {
    return this.entityList.service.getDocumentUrlForEntity(Album, this.entity);
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
}
