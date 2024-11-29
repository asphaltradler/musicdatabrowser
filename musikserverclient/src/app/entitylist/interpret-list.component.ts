import {Component} from '@angular/core';
import {EntityListComponent} from './entity-list.component';
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
export class InterpretListComponent extends EntityListComponent<Interpret> {
  constructor(interpretService: InterpretService, route: ActivatedRoute, router: Router) {
    super(interpretService, route, router);
  }
}
