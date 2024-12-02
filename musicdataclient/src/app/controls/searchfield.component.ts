import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AbstractEntity} from '../entities/abstractEntity';
import {Album} from '../entities/album';
import {Track} from '../entities/track';
import {Composer} from '../entities/composer';
import {Work} from '../entities/work';
import {Genre} from '../entities/genre';
import {Artist} from '../entities/artist';
import {NgForOf} from '@angular/common';
import {appDefaults} from '../../config/config';

@Component({
  standalone: true,
  selector: 'app-searchfield',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf
  ],
  templateUrl: './searchfield.component.html',
  styles: ['input.form-control {width:10%}']
})
export class SearchfieldComponent implements OnInit {
  public static searchEntities: typeof AbstractEntity[] = [Album, Track, Composer, Work, Genre, Artist];
  searchEntities = SearchfieldComponent.searchEntities;

  @Input({required:true}) thisEntity!: typeof AbstractEntity;

  @Input({required:true}) searchEntity!: typeof AbstractEntity;
  @Output() searchEntityChange = new EventEmitter<typeof AbstractEntity>();

  @Input({required:true}) pageSize!: number;
  @Output() pageSizeChange = new EventEmitter<number>();

  @Input() searchString?: string;
  @Output() searchStringChange = new EventEmitter<string>();

  @Input() filterString?: string;
  @Output() filterStringChange = new EventEmitter<string>();

  searchForm = new FormGroup({
    searchEntitySelector: new FormControl<typeof AbstractEntity>(AbstractEntity),
    pageSizeSelector: new FormControl(0),
    searchField: new FormControl(''),
    filterField: new FormControl(''),
  });

  ngOnInit(): void {
    this.searchForm.patchValue({
      searchEntitySelector: this.searchEntity,
      pageSizeSelector: this.pageSize,
      searchField: this.searchString,
      filterField: this.filterString
    }, { emitEvent: false });
  }

  updateSearchEntity() {
    this.searchEntity = this.searchForm.value.searchEntitySelector || AbstractEntity;
    this.searchEntityChange.emit(this.searchEntity);
    this.clearSearchText();
  }

  clearSearchText() {
    this.searchForm.controls.searchField.reset();
    this.updateSearchField();
  }

  updateSearchField() {
    this.searchString = this.searchForm.value.searchField || '';
    this.searchStringChange.emit(this.searchString);
  }

  clearFilter() {
    this.searchForm.controls.filterField.reset();
    this.updateFilter();
  }

  updateFilter() {
    this.filterString = this.searchForm.value.filterField || '';
    this.filterStringChange.emit(this.filterString);
  }

  updatePageSize() {
    this.pageSize = this.searchForm.value.pageSizeSelector || 0;
    this.pageSizeChange.emit(this.pageSize);
  }

  protected readonly appDefaults = appDefaults;
}
