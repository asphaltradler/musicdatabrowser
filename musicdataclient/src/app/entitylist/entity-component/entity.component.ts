import {ChangeDetectionStrategy, Component, ElementRef, Input, input} from '@angular/core';
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
/**
 * Generic Angular component for displaying and interacting with a single entity of type `ENTITY`.
 *
 * @typeParam ENTITY - The type of the entity, extending `AbstractEntity`.
 *
 * @template ENTITY
 *
 * @remarks
 * This component is designed to be used within an `EntityListComponent` context.
 * It manages the display, visibility, and navigation for a single entity instance.
 *
 * @example
 * ```html
 * <app-entity [entity]="myEntity"></app-entity>
 * ```
 *
 * @property entity - The entity instance to display. Setting this updates the component's state and visibility.
 * @property hidden - Controls the visibility of the component in the DOM.
 *
 * @method getOtherEntitiesByThisId - Finds related entities of a different type by this entity's ID.
 * @method getAlbumartUrl - Returns the URL for the entity's album art, if available.
 * @method navigateToDetails - Navigates to the details view for this entity.
 * @method getIdForEntity - Static helper to generate a unique DOM id for an entity.
 *
 * @protected
 * @readonly Album, Composer, Artist, Work, Genre, Track - Entity type references for use in templates.
 *
 * @constructor
 * @param hostElement - Reference to the host DOM element.
 * @param router - Angular Router for navigation.
 */
export class EntityComponent<ENTITY extends AbstractEntity> {
  _entity!: ENTITY;

  /**
   * Reference to the required parent EntityListComponent instance for the current entity.
   *
   * @remarks
   * This property is initialized using the `input.required` decorator, ensuring that
   * the parent EntityListComponent of type `ENTITY` is always provided and accessible.
   *
   * @typeParam ENTITY - The type of the entity managed by the EntityListComponent.
   */
  entityList = input.required<EntityListComponent<ENTITY>>();
  @Input({required: true}) set entity(entity: ENTITY) {
    this._entity = entity;
    this.hostElement.nativeElement.setAttribute('id', EntityComponent.getIdForEntity(this.entityList(), entity));
    //this.hostElement.nativeElement.setAttribute('class', this.entityList.entityType.entityName);
    this.hidden = this.entityList().isEntityFiltered(entity);
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
    return this.entityList().service.findByOtherId(otherEntityType, this.entityList().entityType!, this.entity.id);
  }

  get entity(): ENTITY {
    return this._entity;
  }

  getAlbumartUrl() {
    if (this.entity.albumartId) {
      return this.entityList().service.getThumbnailUrl(this.entity.albumartId.valueOf());
    }
    return "";
  }

  navigateToDetails() {
    this.entityList().navigateToDetails(this.entity);
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
