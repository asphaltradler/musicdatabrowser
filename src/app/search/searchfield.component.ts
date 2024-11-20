import {Component, input, InputSignal, OnInit, output} from '@angular/core';
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
  styles: ['input.form-control {width:50%}']
})
export class SearchfieldComponent implements OnInit {
  public static searchEntities: typeof AbstractEntity[] = [Album, Track, Komponist, Werk, Genre, Interpret];
  searchEntities = SearchfieldComponent.searchEntities;

  searchEntity = output<typeof AbstractEntity>();
  searchText = output<string>();
  selection = input<typeof AbstractEntity>();

  searchForm = new FormGroup({
    entitySelector: new FormControl<typeof AbstractEntity>(AbstractEntity),
    searchField: new FormControl('')
  });

  ngOnInit() {
    //default setzen
    this.searchForm.controls.entitySelector.setValue(this.selection() || null);
  }

  handleSelection() {
    this.clearSearchText();
    if (this.searchForm.value.entitySelector) {
      this.searchEntity.emit(this.searchForm.value.entitySelector);
    }
  }

  clearSearchText() {
    this.searchForm.controls.searchField.reset();
  }

  handleSubmit() {
    this.searchText.emit(this.searchForm.value.searchField || '');
  }
}
