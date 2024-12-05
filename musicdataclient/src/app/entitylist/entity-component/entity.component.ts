import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {NgForOf} from '@angular/common';
import {AbstractEntity} from '../../entities/abstractEntity';
import {Album} from '../../entities/album';
import {Composer} from '../../entities/composer';
import {Artist} from '../../entities/artist';
import {Work} from '../../entities/work';
import {Genre} from '../../entities/genre';
import {Track} from '../../entities/track';
import {EntityListComponent} from '../entity-list.component';

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

  protected readonly Album = Album;
  protected readonly Composer = Composer;
  protected readonly Artist = Artist;
  protected readonly Work = Work;
  protected readonly Genre = Genre;
  protected readonly Track = Track;
}
