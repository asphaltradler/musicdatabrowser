import {Component} from '@angular/core';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchfieldComponent} from '../search/searchfield.component';
import {AbstractEntityList} from './abstractEntityList';
import {ActivatedRoute} from '@angular/router';

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
export class KomponistListComponent extends AbstractEntityList<Komponist> {
  constructor(service: KomponistService, route: ActivatedRoute) {
    super(service, route);
  }
}
