import {Component, input, InputSignal, output} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AbstractEntity} from '../entities/abstractEntity';
import {Album} from '../entities/album';
import {Track} from '../entities/track';
import {Komponist} from '../entities/komponist';
import {Werk} from '../entities/werk';
import {Genre} from '../entities/genre';
import {Interpret} from '../entities/interpret';
import {NgForOf} from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-searchfield',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf
  ],
  templateUrl: './searchfield.component.html',
})
export class SearchfieldComponent {
  public static searchEntities: typeof AbstractEntity[] = [Album, Track, Komponist, Werk, Genre, Interpret];
  searchEntities = SearchfieldComponent.searchEntities;

  searchEntityName = output<string>();
  searchText = output<string>();
  selection = input<string>();

  searchForm = new FormGroup({
    entitySelector: new FormControl(''),
    searchField: new FormControl('')
  });

  handleSelection() {
    this.clearSearchText();
    this.searchEntityName.emit(this.searchForm.value.entitySelector || '');
  }

  clearSearchText() {
    this.searchForm.controls.searchField.reset();
  }

  handleSubmit() {
    this.searchText.emit(this.searchForm.value.searchField || '');
  }
}
