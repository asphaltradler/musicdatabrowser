import {Component} from '@angular/core';
import {AbstractEntityList} from './abstractEntityList';
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
export class InterpretListComponent extends AbstractEntityList<Interpret> {
  constructor(interpretService: InterpretService) {
    super(interpretService);
    this.namePlural = 'Interpreten';
  }
}
