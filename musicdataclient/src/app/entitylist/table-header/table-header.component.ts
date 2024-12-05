import {Component, Input} from '@angular/core';
import {AbstractEntity} from '../../entities/abstractEntity';
import {EntityListComponent} from '../entity-list.component';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'tr.app-table-header',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './table-header.component.html',
  styles: '.text-right {text-align: right;}'
})
export class TableHeaderComponent<E extends AbstractEntity> {
  @Input({required: true}) entityList!: EntityListComponent<E>;
}
