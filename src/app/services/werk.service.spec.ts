import {TestBed} from '@angular/core/testing';

import {WerkService} from './werk.service';

describe('WerkService', () => {
  let service: WerkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WerkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
