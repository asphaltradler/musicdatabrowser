import {Component, output} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-searchfield',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './searchfield.component.html',
})
export class SearchfieldComponent {
  searchText = output<string>();

  searchForm = new FormGroup({
    searchField: new FormControl('')
  });

  handleSubmit() {
    this.searchText.emit(this.searchForm.value.searchField || '');
  }
}
