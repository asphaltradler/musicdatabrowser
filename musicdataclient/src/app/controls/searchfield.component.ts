import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
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
export class SearchfieldComponent implements OnChanges {
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

  searchForm = new FormGroup({
    searchEntitySelector: new FormControl<typeof AbstractEntity>(AbstractEntity),
    pageSizeSelector: new FormControl(0),
    searchField: new FormControl(''),
    filterField: new FormControl(''),
  });

  ngOnChanges(): void {
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
    this.updateSearchField();
  }

  clearSearchText() {
    this.searchForm.controls.searchField.reset();
  }

  updateSearchField() {
    this.searchString = this.searchForm.value.searchField || '';
    this.searchStringChange.emit(this.searchString);
  }

  clearFilter() {
    this.searchForm.controls.filterField.reset();
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
