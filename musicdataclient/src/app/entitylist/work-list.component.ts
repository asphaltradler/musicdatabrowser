import {Component} from '@angular/core';
import {EntityListComponent} from './entity-list.component';
import {Work} from '../entities/work';
import {WorkService} from '../services/work.service';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';
import {PagingComponent} from '../controls/paging.component';

@Component({
  selector: 'app-work-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    PagingComponent,
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
