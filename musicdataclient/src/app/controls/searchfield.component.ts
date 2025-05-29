import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AbstractEntity} from '../entities/abstractEntity';
import {NgForOf} from '@angular/common';
import {appDefaults} from '../../config/config';
import {allEntities} from '../../config/utilities';

@Component({
  standalone: true,
  selector: 'app-searchfield',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf
  ],
  templateUrl: './searchfield.component.html',
  styles: ['input.minwidth,select.minwidth {width:120px;flex-grow: 0.1}']
})
export class SearchfieldComponent {
  searchEntities = allEntities;

  @Input({required:true}) thisEntity!: typeof AbstractEntity;

  @Input({required:true}) searchEntity!: typeof AbstractEntity;
  @Output() searchEntityChange = new EventEmitter<typeof AbstractEntity>();

  @Input({required:true}) pageSize!: number;
  @Output() pageSizeChange = new EventEmitter<number>();

  @Input() searchString: string = '';
  @Output() searchStringChange = new EventEmitter<string>();

  @Input() filterString: string = '';
  @Output() filterStringChange = new EventEmitter<string>();

  clearSearchText() {
    this.searchString = '';
    this.searchStringChange.emit(this.searchString);
  }

  clearFilter() {
    this.filterString = '';
    this.filterStringChange.emit(this.filterString);
  }

  protected readonly appDefaults = appDefaults;
}
