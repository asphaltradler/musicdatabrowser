import {Component, Input} from '@angular/core';
import {EntityListComponent} from '../entitylist/entity-list.component';

@Component({
  selector: 'app-paging',
  standalone: true,
  imports: [],
  templateUrl: './paging.component.html',
  styleUrl: './paging.component.css'
})
export class PagingComponent {
  @Input({required:true})
  entityList!: EntityListComponent<any>;

  handlePrevious() {
    this.entityList.searchPreviousPage();
  }

  handleNext() {
    this.entityList.searchNextPage();
  }
}
