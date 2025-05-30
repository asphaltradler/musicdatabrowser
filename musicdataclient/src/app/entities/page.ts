import {AbstractEntity} from './abstractEntity';

/**
 * Represents a paginated response containing a subset of entities of type `E`.
 *
 * @typeParam E - The type of entity contained in the page, extending `AbstractEntity`.
 *
 * @property content - The list of entities on the current page.
 * @property empty - Indicates whether the page contains any entities.
 * @property first - Indicates if this is the first page.
 * @property last - Indicates if this is the last page.
 * @property number - The current page number (zero-based).
 * @property totalPages - The total number of available pages.
 * @property numberOfElements - The number of entities returned in the current page (may be less than `size`).
 * @property totalElements - The total number of entities across all pages.
 * @property size - The requested size of the page (number of entities per page).
 */
export class Page<E extends AbstractEntity> {
  content!: E[];
  empty!: boolean;

  first!: boolean;
  last!: boolean;
  //pageNumber, leider nicht sehr sprechend
  number!: number;
  //wieviele Pages gibt es insgesamt?
  totalPages!: number;
  //die tatsächlich zurückgegebene Anzahl, kann also weniger als pageSize sein
  numberOfElements!: number;
  //die Gesamtanzahl aller Elemente in allen Pages
  totalElements!: number;
  //size ist die angeforderte pageSize Größe
  size!: number;
}
