import {Component} from '@angular/core';
import {EntityListComponent} from './entity-list.component';
import {Work} from '../entities/work';
import {WorkService} from '../services/work.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-work-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class WorkListComponent extends EntityListComponent<Work> {
  constructor(service: WorkService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
  }
}
