import {AbstractEntity} from './abstractEntity';
import {Album} from './album';
import {Composer} from './composer';
import {Artist} from './artist';
import {Work} from './work';
import {Genre} from './genre';
import {Document} from './document';

export class Track extends AbstractEntity {
  static override entityName = 'track';
  static override namePlural = 'Tracks';

  tracknumber?: number;
  path!: string;
  lastModifiedDate!: string;
  publisher?: string;
  publishedDate?: string;
  lengthInSeconds!: number;
  album?: Album;
  composer?: Composer;
  artists?: Artist[];
  genres?: Genre[];
  work?: Work;
  albumart?: Document;
  booklet?: Document;

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
      return differenceInYears + 'y';
    } else if (differenceInMonths > 1) {
      return differenceInMonths + 'mon';
    } else if (differenceInWeeks > 1) {
      return differenceInWeeks + 'w';
    } else if (differenceInDays > 1) {
      return differenceInDays + 'd';
    } else if (differenceInHours > 1) {
      return differenceInHours + 'h';
    } else if (differenceInMinutes > 1) {
      return differenceInMinutes + 'm';
    } else if (differenceInSeconds > 1) {
      return differenceInSeconds + 's';
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
