import {AbstractEntity} from './abstractEntity';
import {Album} from './album';
import {Composer} from './composer';
import {Artist} from './artist';
import {Work} from './work';
import {Genre} from './genre';

export class Track extends AbstractEntity {
  static override entityName = 'track';
  static override namePlural = 'Tracks';

  tracknumber?: number;
  path!: string;
  fileModifiedDate!: string;
  publisher?: string;
  publishedDate?: string;
  lengthInSeconds!: number;
  album?: Album;
  composer?: Composer;
  artists?: Artist[];
  genres?: Genre[];
  work?: Work;
  bookletId?: Number;
  bookletName?: string;

  static getLastModificationSince(t: Track) {
    const now = new Date();
    const date = new Date(t.fileModifiedDate);
    const differenceInMilliseconds = now.getTime() - date.getTime();
    const differenceInSeconds = differenceInMilliseconds / 1000;
    const differenceInMinutes = differenceInSeconds / 60;
    const differenceInHours = differenceInMinutes / 60;
    const differenceInDays = differenceInHours / 24;
    const differenceInWeeks = differenceInDays / 7;
    const differenceInMonths = differenceInDays / 30;
    const differenceInYears = differenceInDays / 365;
    if (differenceInYears > 1) {
      return differenceInYears.toFixed(1) + 'y';
    } else if (differenceInMonths > 1) {
      return differenceInMonths.toFixed(1) + 'mon';
    } else if (differenceInWeeks > 1) {
      return differenceInWeeks.toFixed(1) + 'w';
    } else if (differenceInDays > 1) {
      return differenceInDays.toFixed(1) + 'd';
    } else if (differenceInHours > 1) {
      return differenceInHours.toFixed(1) + 'h';
    } else if (differenceInMinutes > 1) {
      return differenceInMinutes.toFixed(1) + 'm';
    } else if (differenceInSeconds > 1) {
      return differenceInSeconds.toFixed(1) + 's';
    } else {
      return 'JETZT';
    }
  }

  static getLength(t: Track) {
    const len = t.lengthInSeconds;
    let min = Math.floor(len / 60).toString();
    let sec = (len % 60).toString();
    if (sec.length <= 1) {
      sec = '0' + sec;
    }
    return `${min}:${sec}`;
  }
}
