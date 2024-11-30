import {Component, Input, OnInit} from '@angular/core';
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

  searchForm = new FormGroup({
    entitySelector: new FormControl<typeof AbstractEntity>(AbstractEntity),
    searchField: new FormControl(''),
    filterField: new FormControl('')
  });

  ngOnInit() {
    //default setzen
    this.searchForm.controls.entitySelector.setValue(this.entityList.entityType);
    this.handleSelection();
  }

  handleSelection() {
    this.clearSearchText();
    if (EntityListComponent.USE_LOCAL_FILTERING_INSTEAD_SEARCH) {
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

  clearFilter() {
    this.searchForm.controls.filterField.reset();
    this.handleFilter();
  }

  handleFilter() {
    this.entityList.setFilter(this.searchForm.value.filterField || '');
  }
}
