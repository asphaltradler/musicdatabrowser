import {ChangeDetectionStrategy, Component, ElementRef, Input} from '@angular/core';
import {NgForOf} from '@angular/common';
import {AbstractEntity} from '../../entities/abstractEntity';
import {Album} from '../../entities/album';
import {Composer} from '../../entities/composer';
import {Artist} from '../../entities/artist';
import {Work} from '../../entities/work';
import {Genre} from '../../entities/genre';
import {Track} from '../../entities/track';
import {EntityListComponent} from '../entity-list.component';
import {appDefaults} from '../../../config/config';

@Component({
  selector: 'tr.app-entity-row',
  standalone: true,
  imports: [NgForOf],
  templateUrl: './entity.component.html',
  styleUrls: ['../entity-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EntityComponent<ENTITY extends AbstractEntity> {
  @Input({required: true}) entity!: ENTITY;
  @Input({required: true}) entityList!: EntityListComponent<ENTITY>;
  //TODO muss das wirklich so kompliziert sein? wieso kann ich das nicht mit [hidden] setzen?
  @Input() set hidden(hide: boolean) {
    if (hide) {
      this.hostElement.nativeElement.setAttribute('hidden', '');
    } else {
      this.hostElement.nativeElement.removeAttribute('hidden');
    }
  }

  constructor(protected hostElement: ElementRef) {}

  getOtherEntitiesByThisId(otherEntityType: typeof AbstractEntity) {
    return this.entityList.service.findByOtherId(otherEntityType, this.entityList.entityType, this.entity.id, 0, appDefaults.maxPageSizeForLists);
  }

  getAlbumartUrl() {
    if (this.entity.albumartId) {
      return this.entityList.service.getDocumentUrl(this.entity.albumartId.valueOf());
    }
    return "";
  }

  protected readonly Album = Album;
  protected readonly Composer = Composer;
  protected readonly Artist = Artist;
  protected readonly Work = Work;
  protected readonly Genre = Genre;
  protected readonly Track = Track;
}
