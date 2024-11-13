import {Component} from '@angular/core';
import {AbstractEntityList} from './abstractEntityList';
import {Interpret} from '../entities/interpret';
import {InterpretService} from '../services/interpret.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-interpret-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class InterpretListComponent extends AbstractEntityList<Interpret> {
  constructor(interpretService: InterpretService, route: ActivatedRoute, router: Router) {
    super(interpretService, route, router);
  }
}
