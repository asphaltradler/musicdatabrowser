import {Component} from '@angular/core';
import {EntityListComponent} from './abstractEntityList';
import {Interpret} from '../entities/interpret';
import {InterpretService} from '../services/interpret.service';
import {SearchfieldComponent} from '../search/searchfield.component';

@Component({
  selector: 'app-interpret-list',
  standalone: true,
  imports: [
    SearchfieldComponent
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class InterpretListComponent extends EntityListComponent<Interpret> {
  constructor(interpretService: InterpretService) {
    super(interpretService);
    this.name = Interpret.name;
    this.namePlural = "Interpreten";
  }
}
