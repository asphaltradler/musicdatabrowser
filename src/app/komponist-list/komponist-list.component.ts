import {Component, OnInit} from '@angular/core';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-komponist-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './komponist-list.component.html',
  styleUrl: './komponist-list.component.css'
})
export class KomponistListComponent implements OnInit {

  komponisten: Komponist[] = [];
  filter: string = '';

  constructor(private komponistService: KomponistService) {
  }

  ngOnInit() {
    this.filter = '';
    this.search();
  }

  private search() {
    this.komponistService.find(this.filter).subscribe(data => {
      this.komponisten = data;
    });
  }

  searchForm = new FormGroup({
    searchText: new FormControl('')
  });

  getTitle() {
    return "Alle Komponisten" + (this.filter ? " mit Name enthält " + this.filter : "");
  }

  handleSubmit() {
    this.filter = this.searchForm.value.searchText || '';
    this.search();
  }
}
