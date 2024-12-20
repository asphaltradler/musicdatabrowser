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
import {Router} from '@angular/router';

@Component({
  selector: 'tr.app-entity-row',
  standalone: true,
  imports: [NgForOf],
  templateUrl: './entity.component.html',
  styleUrls: ['../entity-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EntityComponent<ENTITY extends AbstractEntity> {
  _entity!: ENTITY;

  @Input({required: true}) entityList!: EntityListComponent<ENTITY>;
  @Input({required: true}) set entity(entity: ENTITY) {
    this._entity = entity;
    this.hostElement.nativeElement.setAttribute('id', EntityComponent.getIdForEntity(this.entityList, entity));
    //this.hostElement.nativeElement.setAttribute('class', this.entityList.entityType.entityName);
    this.hidden = this.entityList?.isEntityFiltered(entity);
  }

  @Input() set hidden(hide: boolean) {
    if (hide) {
      this.hostElement.nativeElement.setAttribute('hidden', '');
    } else {
      this.hostElement.nativeElement.removeAttribute('hidden');
    }
  }

  constructor(protected hostElement: ElementRef<HTMLElement>, protected router: Router) {
  }

  getOtherEntitiesByThisId(otherEntityType: typeof AbstractEntity) {
    return this.entityList.service.findByOtherId(otherEntityType, this.entityList.entityType!, this.entity.id);
  }

  get entity(): ENTITY {
    return this._entity;
  }

  getAlbumartUrl() {
    if (this.entity.albumartId) {
      return this.entityList.service.getDocumentUrl(this.entity.albumartId.valueOf());
    }
    return "";
  }

  navigateToDetails() {
    this.entityList.navigateToDetails(this.entity);
  }

  public static getIdForEntity<ENTITY extends AbstractEntity>(entityList: EntityListComponent<ENTITY>, entity: ENTITY): string {
    return `${entityList?.entityType.entityName}-${entity.id}`;
  }

  protected readonly Album = Album;
  protected readonly Composer = Composer;
  protected readonly Artist = Artist;
  protected readonly Work = Work;
  protected readonly Genre = Genre;
  protected readonly Track = Track;
}
