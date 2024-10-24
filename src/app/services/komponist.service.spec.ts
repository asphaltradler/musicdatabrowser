import {TestBed} from '@angular/core/testing';

import {KomponistService} from './komponist.service';
import {provideHttpClient} from '@angular/common/http';

describe('KomponistService', () => {
  let service: KomponistService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KomponistService, provideHttpClient()]
    });
    service = TestBed.inject(KomponistService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
