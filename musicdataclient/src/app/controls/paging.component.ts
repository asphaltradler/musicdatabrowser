import {Component, input, Input} from '@angular/core';
import { EntityListComponent } from '../entitylist/entity-list.component';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-paging',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './paging.component.html',
  styleUrls: ['./paging.component.css']
})
export class PagingComponent {
  private static readonly NO_RESULTS_MESSAGE = "< Keine Ergebnisse >";

  entityList = input.required<EntityListComponent<any>>();

  handlePrevious(): void {
    this.entityList().searchPreviousPage();
  }

  handleNext(): void {
    this.entityList().searchNextPage();
  }

  getPageTitle(): string {
    if (this.hasValidPage()) {
      return `Seite ${this.entityList()!.page!.number + 1} von ${this.entityList()!.page!.totalPages}`;
    }
    return PagingComponent.NO_RESULTS_MESSAGE;
  }

  private hasValidPage(): boolean {
    return !this.entityList().page?.empty;
  }
}
