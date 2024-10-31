import {TestBed} from '@angular/core/testing';

import {InterpretService} from './interpret.service';
import {provideHttpClient} from '@angular/common/http';

describe('InterpretService', () => {
  let service: InterpretService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [InterpretService, provideHttpClient()]
    });
    service = TestBed.inject(InterpretService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
