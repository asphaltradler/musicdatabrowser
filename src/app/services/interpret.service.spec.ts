import {TestBed} from '@angular/core/testing';

import {InterpretService} from './interpret.service';

describe('InterpretService', () => {
  let service: InterpretService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InterpretService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
