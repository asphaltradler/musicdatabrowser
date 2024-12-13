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
import {Params, Router} from '@angular/router';
import {detailsPath, paramEntity, paramSourceEntity} from '../../../config/utilities';

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
    this.hidden = this.entityList?.isEntityFiltered(entity);
  }

  @Input() set hidden(hide: boolean) {
    if (hide) {
      this.hostElement.nativeElement.setAttribute('hidden', '');
    } else {
      this.hostElement.nativeElement.removeAttribute('hidden');
    }
  }
  @Input() set id(id: string) {
    this.hostElement.nativeElement.setAttribute('id', id);
  }

  constructor(protected hostElement: ElementRef, protected router: Router) {
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
    console.log(`Navigiere zu Details für ${this.entityList.entityType.entityName} id ${this.entity.id}='${this.entity.name}'`);
    const params: Params = {};
    params[paramEntity] = this.entity;
    params[paramSourceEntity] = this.entity;
    this.router.navigate([this.entityList.entityType.entityName, this.entity.id, detailsPath],
      { state: params });
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
