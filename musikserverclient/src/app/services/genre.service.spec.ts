import {TestBed} from '@angular/core/testing';

import {GenreService} from './genre.service';
import {provideHttpClient} from '@angular/common/http';

describe('GenreService', () => {
  let service: GenreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        GenreService, provideHttpClient()
      ]});
    service = TestBed.inject(GenreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
