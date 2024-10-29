import {Component} from '@angular/core';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchlistComponent} from '../searchlist/searchlist.component';
import {EntityListComponent} from './abstractEntityList';
import {Observable} from 'rxjs';

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
export class KomponistListComponent extends EntityListComponent<Komponist> {
  constructor(service: KomponistService) {
    super(service);
    this.name = Komponist.name;
    this.namePlural = "Komponisten";
  }

  public override search(searchText: string): Observable<Komponist[]> {
    const obs = super.search(searchText);
    return obs;
  }
}
