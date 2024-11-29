import {TestBed} from '@angular/core/testing';

import {ArtistService} from './artist.service';
import {provideHttpClient} from '@angular/common/http';

describe('ArtistService', () => {
  let service: ArtistService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ArtistService, provideHttpClient()]
    });
    service = TestBed.inject(ArtistService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
