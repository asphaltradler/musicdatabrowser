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
      return this.entityList.service.getDocumentUrl(this.entity.bookletId.valueOf());
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
