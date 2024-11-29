import {Component} from '@angular/core';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-komponist-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    NgForOf,
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class KomponistListComponent extends EntityListComponent<Komponist> {
  constructor(service: KomponistService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
  }
}
