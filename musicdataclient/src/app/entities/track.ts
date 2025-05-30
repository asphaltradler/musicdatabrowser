import {AbstractEntity} from './abstractEntity';
import {Album} from './album';
import {Composer} from './composer';
import {Artist} from './artist';
import {Work} from './work';
import {Genre} from './genre';

/**
 * Represents a music track entity with metadata such as track number, file path, modification date,
 * publisher, published date, duration, album, composer, artists, genres, work, and booklet information.
 *
 * @extends AbstractEntity
 *
 * @property {number} [tracknumber] - The track number within the album or collection.
 * @property {string} path - The file system path to the track.
 * @property {string} fileModifiedDate - The last modification date of the track file (ISO string).
 * @property {string} [publisher] - The publisher of the track.
 * @property {string} [publishedDate] - The date the track was published (ISO string).
 * @property {number} lengthInSeconds - The duration of the track in seconds.
 * @property {Album} [album] - The album to which the track belongs.
 * @property {Composer} [composer] - The composer of the track.
 * @property {Artist[]} [artists] - The artists who performed the track.
 * @property {Genre[]} [genres] - The genres associated with the track.
 * @property {Work} [work] - The musical work associated with the track.
 * @property {Number} [bookletId] - The ID of the booklet associated with the track.
 * @property {string} [bookletName] - The name of the booklet associated with the track.
 */
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

  /**
   * Returns a human-readable string representing the time elapsed since the file was last modified.
   * @param {Track} t - The track instance.
   * @returns {string} The elapsed time in a compact format (e.g., '2d', '3h', 'JETZT').
   */
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

  /**
   * Returns the length of the track formatted as "mm:ss".
   * @param {Track} t - The track instance.
   * @returns {string} The formatted length string.
   */
  public static getLength(t: Track) {
    const length = Math.max(t.lengthInSeconds | 0, 0);
    const min = Math.floor(length / 60);
    const sec = (length % 60).toString().padStart(2, '0');
    return `${min}:${sec}`;
  }
}
