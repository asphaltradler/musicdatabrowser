import {AbstractEntity} from './abstractEntity';
import {Album} from './album';
import {Komponist} from './komponist';
import {Interpret} from './interpret';
import {Werk} from './werk';
import {Genre} from './genre';

export class Track extends AbstractEntity {
  static override entityName = 'track';
  static override namePlural = 'Tracks';

  tracknumber?: number;
  path!: string;
  lastModifiedDate!: string;
  album?: Album;
  komponist?: Komponist;
  interpreten?: Interpret[];
  genres?: Genre[];
  werk?: Werk;

  static getLastModificationSince(t: Track) {
    const now = new Date();
    const date = new Date(t.lastModifiedDate);
    const differenceInMilliseconds = now.getTime() - date.getTime();
    const differenceInSeconds = differenceInMilliseconds / 1000;
    const differenceInMinutes = differenceInSeconds / 60;
    const differenceInHours = differenceInMinutes / 60;
    const differenceInDays = Math.floor(differenceInHours / 24);
    const differenceInWeeks = Math.floor(differenceInDays / 7);
    const differenceInMonths = Math.floor(differenceInDays / 30);
    const differenceInYears = Math.floor(differenceInDays / 365);
    if (differenceInYears > 1) {
      return differenceInYears + ' Jahre';
    } else if (differenceInMonths > 1) {
      return differenceInMonths + ' Monate';
    } else if (differenceInWeeks > 1) {
      return differenceInWeeks + ' Wochen';
    } else if (differenceInDays > 1) {
      return differenceInDays + ' Tage';
    } else if (differenceInHours > 1) {
      return differenceInHours + ' Stunden';
    } else if (differenceInMinutes > 1) {
      return differenceInMinutes + ' Minuten';
    } else if (differenceInSeconds > 1) {
      return differenceInSeconds + ' Sekunden';
    } else {
      return 'JETZT';
    }
  }
}
