import {Component, Input} from '@angular/core';
import {EntityListComponent} from '../entitylist/entity-list.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-paging',
  standalone: true,
  imports: [
    NgIf
  ],
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

  getPageTitle() {
    if (this.entityList.page && !this.entityList.page.empty) {
      return `Seite ${this.entityList.page.number + 1} von ${this.entityList.page.totalPages}`;
    }
    return "< Keine Ergebnisse >";
  }
}
