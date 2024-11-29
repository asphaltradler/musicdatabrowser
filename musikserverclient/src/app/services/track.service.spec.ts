import {TestBed} from '@angular/core/testing';

import {TrackService} from './track.service';
import {provideHttpClient} from '@angular/common/http';
import {Track} from '../entities/track';

describe('TrackService', () => {
  let service: TrackService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Track, provideHttpClient()]
    });
    service = TestBed.inject(TrackService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
