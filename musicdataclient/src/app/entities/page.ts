import {AbstractEntity} from './abstractEntity';
import {EntityModel} from '../entitymodels/entity-model';

export class Page<E extends AbstractEntity> {
  content!: E[];
  modelContent!: EntityModel<E>[];
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
