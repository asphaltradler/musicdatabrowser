import {Component} from '@angular/core';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchfieldComponent} from '../search/searchfield.component';
import {EntityListComponent} from './abstractEntityList';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-komponist-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SearchfieldComponent
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class KomponistListComponent extends EntityListComponent<Komponist> {
  constructor(service: KomponistService) {
    super(service);
    this.name = Komponist.name;
    this.namePlural = 'Komponisten';
  }

  public override search(searchText: string): Observable<Komponist[]> {
    return super.search(searchText);
  }
}
