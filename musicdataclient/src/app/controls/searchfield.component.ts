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
import {EntityListComponent} from '../entitylist/entity-list.component';
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

  @Input({required:true})
  entityList!: EntityListComponent<any>;

  @Input() filterString?: string;
  @Output() filterStringChange = new EventEmitter<string>();

  searchForm = new FormGroup({
    entitySelector: new FormControl<typeof AbstractEntity>(AbstractEntity),
    pageSizeSelector: new FormControl(),
    searchField: new FormControl(''),
    filterField: new FormControl('')
  });

  ngOnInit() {
    //defaults setzen
    this.searchForm.controls.entitySelector.setValue(this.entityList.entityType);
    this.searchForm.controls.pageSizeSelector.setValue(this.entityList.pageSize);
    this.handleSelection();
  }

  handleSelection() {
    this.clearSearchText();
    if (appDefaults.useLocalFilteringInsteadSearch) {
      if (this.searchForm.controls.entitySelector.value === this.entityList.entityType) {
        this.searchForm.controls.filterField.disable();
      } else {
        this.searchForm.controls.filterField.enable();
      }
    }
  }

  clearSearchText() {
    this.searchForm.controls.searchField.reset();
  }

  handleSubmit() {
    //this.searchText.emit(this.searchForm.value.searchField || '');
    const entityType = this.searchForm.value.entitySelector;
    if (entityType) {
      this.entityList.searchByEntityName(entityType, this.searchForm.value.searchField || '');
    }
  }

  handlePageSize() {
    this.entityList.setPageSize(this.searchForm.value.pageSizeSelector);
    this.handleSubmit();
  }

  clearFilter() {
    this.searchForm.controls.filterField.reset();
    this.handleFilter();
  }

  handleFilter() {
    this.entityList.setFilter(this.searchForm.value.filterField || '');
  }

  protected readonly appDefaults = appDefaults;
}
