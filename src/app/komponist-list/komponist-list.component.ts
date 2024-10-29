import {Component, OnInit} from '@angular/core';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchlistComponent} from '../searchlist/searchlist.component';

@Component({
  selector: 'app-komponist-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SearchlistComponent
  ],
  templateUrl: './komponist-list.component.html',
  styleUrl: './komponist-list.component.css'
})
export class KomponistListComponent implements OnInit {

  komponisten: Komponist[] = [];

  constructor(private komponistService: KomponistService, private searchListComponent: SearchlistComponent) {
  }

  public search(searchText: string) {
    console.log("Suche KOMPONIST nach " + searchText);
    this.komponistService.find(searchText).subscribe(data => {
      this.komponisten = data;
    });
  }

  getTitle() {
    const searchText = this.searchListComponent.searchForm.value.searchText;
    return "Alle Komponisten" + (searchText ? " mit Name enthält " + searchText : "");
  }

  ngOnInit(): void {
    this.search('');
  }
}
