import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef} from '@angular/core';
import {Album} from '../../entities/album';
import {NgForOf, NgIf} from '@angular/common';
import {Composer} from '../../entities/composer';
import {Artist} from '../../entities/artist';
import {Genre} from '../../entities/genre';
import {Work} from '../../entities/work';
import {AbstractEntity} from '../../entities/abstractEntity';
import {EntityComponent} from './entity.component';
import {forkJoin} from 'rxjs';
import {Router} from '@angular/router';

@Component({
  selector: 'tr.app-entity-row',
  standalone: true,
  imports: [NgForOf, NgIf],
  templateUrl: './album.component.html',
  styleUrls: ['../entity-list.component.css', 'album.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
/**
 * Component for displaying and managing an Album entity and its related lists (composers, works, genres, artists).
 * 
 * - Uses `IntersectionObserver` to lazily load related entity lists when the component enters the viewport.
 * - Fetches all related entities in parallel using `forkJoin` to minimize UI flicker.
 * - Provides utility methods to retrieve booklet URLs and names, and to access related entities for search.
 * 
 * @template Album The entity type for the album.
 * @extends EntityComponent<Album>
 * 
 * @property {Composer[]} [composers] - List of composers related to the album.
 * @property {Work[]} [works] - List of works related to the album.
 * @property {Genre[]} [genres] - List of genres related to the album.
 * @property {Artist[]} [artists] - List of artists related to the album.
 * @property {IntersectionObserver} obs - Observer for triggering lazy loading when the component is visible.
 * 
 * @constructor
 * @param {ElementRef} hostElement - Reference to the host DOM element.
 * @param {Router} router - Angular router for navigation.
 * @param {ChangeDetectorRef} changeRef - Change detector for triggering view updates.
 * 
 * @method lazyLoadLists Loads all related entity lists when the component becomes visible.
 * @method getBookletUrl Returns the URL for the album's booklet document, if available.
 * @method getBookletName Returns a shortened name for the booklet, optionally truncated to a maximum length.
 * @method getSearchEntities Returns the list of related entities of the specified type.
 */
export class AlbumComponent extends EntityComponent<Album> {
  composers?: Composer[];
  works?: Work[];
  genres?: Genre[];
  artists?: Artist[];

  obs: IntersectionObserver;

  constructor(hostElement: ElementRef, router: Router, private changeRef: ChangeDetectorRef) {
    super(hostElement, router);
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

  getBookletUrl() {
    if (this.entity.bookletId) {
      return this.entityList().service.getDocumentUrl(this.entity.bookletId.valueOf());
    }
    return "";
  }

  getBookletName(maxlen: number = 28) {
    let name = this.entity.bookletName?.replace(/^.*\//, '');
    if (name && name.length > maxlen - 3) {
      name = '...' + name.substring(name.length - maxlen);
    }
    return name;
  }

  getSearchEntities(entity: typeof AbstractEntity): AbstractEntity[] | undefined {
    switch (entity) {
      case Composer: return this.composers;
      case Work: return this.works;
      case Genre: return this.genres;
      case Artist: return this.artists;
    }
    return [];
  }
}
