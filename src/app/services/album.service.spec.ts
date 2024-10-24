import {TestBed} from '@angular/core/testing';

import {AlbumService} from './album.service';
import {provideHttpClient} from '@angular/common/http';

describe('AlbumService', () => {
  let service: AlbumService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AlbumService, provideHttpClient()
      ],
    });
    service = TestBed.inject(AlbumService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
