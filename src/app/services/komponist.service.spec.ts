import { TestBed } from '@angular/core/testing';

import { KomponistService } from './komponist.service';

describe('KomponistService', () => {
  let service: KomponistService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KomponistService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
