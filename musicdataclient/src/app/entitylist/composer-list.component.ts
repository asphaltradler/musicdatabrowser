import {Component} from '@angular/core';
import {Composer} from '../entities/composer';
import {ComposerService} from '../services/composer.service';
import {SearchfieldComponent} from '../search/searchfield.component';
import {EntityListComponent} from './entity-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-composer-list',
  standalone: true,
  imports: [
    SearchfieldComponent,
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
