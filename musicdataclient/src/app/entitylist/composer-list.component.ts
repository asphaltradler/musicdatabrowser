import {Component} from '@angular/core';
import {Composer} from '../entities/composer';
import {ComposerService} from '../services/composer.service';
import {SearchfieldComponent} from '../controls/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';
import {PagingComponent} from '../controls/paging.component';
import {ListHeaderComponent} from './list-header/list-header.component';

@Component({
  selector: 'app-composer-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
    PagingComponent,
    ListHeaderComponent,
    NgForOf,
  ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.css'
})
export class ComposerListComponent extends EntityListComponent<Composer> {
  constructor(service: ComposerService, route: ActivatedRoute, router: Router) {
    super(service, route, router);
  }
}
