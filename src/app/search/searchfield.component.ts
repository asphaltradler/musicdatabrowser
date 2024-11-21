import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AbstractEntity} from '../entities/abstractEntity';
import {Album} from '../entities/album';
import {Track} from '../entities/track';
import {Komponist} from '../entities/komponist';
import {Werk} from '../entities/werk';
import {Genre} from '../entities/genre';
import {Interpret} from '../entities/interpret';
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
  public static searchEntities: typeof AbstractEntity[] = [Album, Track, Komponist, Werk, Genre, Interpret];
  searchEntities = SearchfieldComponent.searchEntities;

  @Input({required:true})
  entityList!: EntityListComponent<any>;

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
    if (this.searchForm.controls.entitySelector.value === this.entityList.entityType) {
      this.searchForm.controls.filterField.disable();
    } else {
      this.searchForm.controls.filterField.enable();
    }
  }

  clearSearchText() {
    this.searchForm.controls.searchField.reset();
    this.handleSubmit();
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
