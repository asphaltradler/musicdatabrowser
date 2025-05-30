import {Track} from './track';

describe('Track', () => {
  it('should create an instance', () => {
    expect(new Track()).toBeTruthy();
  });

  describe('getLastModificationSince', () => {
    function makeTrackWithModifiedDate(date: Date): Track {
      const t = new Track();
      t.fileModifiedDate = date.toISOString();
      t.lengthInSeconds = 0;
      t.path = '';
      return t;
    }

    it('should return "JETZT" for just now', () => {
      const t = makeTrackWithModifiedDate(new Date());
      expect(Track.getLastModificationSince(t)).toBe('JETZT');
    });

    it('should return seconds for less than a minute', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 30 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.?\d*s$/);
    });

    it('should return minutes for less than an hour', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 2 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/2.0m$/);
    });

    it('should return "2.5m" for 2 and a half minute', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 2.5 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toBe('2.5m');
    });

    it('should return hours for less than a day', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 2 * 60 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.?\d*h$/);
    });

    it('should return days for less than a week', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 3 * 24 * 60 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.?\d*d$/);
    });

    it('should return weeks for less than a month', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 2 * 7 * 24 * 60 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.?\d*w$/);
    });

    it('should return months for less than a year', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 2 * 30 * 24 * 60 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.?\d*mon$/);
    });

    it('should return years for more than a year', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 2 * 365 * 24 * 60 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.?\d*y$/);
    });

    it('should return year fractions with 1 decimal point', () => {
      const t = makeTrackWithModifiedDate(new Date(Date.now() - 10.5 * 365 * 24 * 60 * 60 * 1000));
      expect(Track.getLastModificationSince(t)).toMatch(/^\d+\.\dy$/);
    });
  });

  describe('getLength', () => {
    function makeTrackWithLength(lengthInSeconds: number): Track {
      const t = new Track();
      t.lengthInSeconds = lengthInSeconds;
      t.fileModifiedDate = new Date().toISOString();
      t.path = '';
      return t;
    }

    it('should return "0:00" when lengthInSeconds is 0', () => {
      const t = makeTrackWithLength(0);
      expect(Track.getLength(t)).toBe('0:00');
    });

    it('should return "0:05" for 5 seconds', () => {
      const t = makeTrackWithLength(5);
      expect(Track.getLength(t)).toBe('0:05');
    });

    it('should return "1:00" for 60 seconds', () => {
      const t = makeTrackWithLength(60);
      expect(Track.getLength(t)).toBe('1:00');
    });

    it('should return "2:03" for 123 seconds', () => {
      const t = makeTrackWithLength(123);
      expect(Track.getLength(t)).toBe('2:03');
    });

    it('should pad seconds with leading zero if needed', () => {
      const t = makeTrackWithLength(71);
      expect(Track.getLength(t)).toBe('1:11');
    });

    it('should handle undefined lengthInSeconds as "0:00"', () => {
      const t = new Track();
      t.fileModifiedDate = new Date().toISOString();
      t.path = '';
      expect(Track.getLength(t)).toBe('0:00');
    });

    it('should handle negative lengthInSeconds as "0:00"', () => {
      const t = makeTrackWithLength(-10);
      expect(Track.getLength(t)).toBe('0:00');
    });
  });
});
