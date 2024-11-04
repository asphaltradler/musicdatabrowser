import {Component} from '@angular/core';
import {AbstractEntityList} from './abstractEntityList';
import {Werk} from '../entities/werk';
import {WerkService} from '../services/werk.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchfieldComponent} from '../search/searchfield.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-werk-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SearchfieldComponent
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class WerkListComponent extends AbstractEntityList<Werk> {
  constructor(service: WerkService, route: ActivatedRoute) {
    super(service, route);
    this.namePlural = 'Werke';
  }

  public override search(searchText: string) {
    return super.search(searchText);
  }
}
