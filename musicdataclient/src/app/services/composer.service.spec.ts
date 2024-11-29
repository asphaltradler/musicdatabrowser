import {TestBed} from '@angular/core/testing';

import {ComposerService} from './composer.service';
import {provideHttpClient} from '@angular/common/http';

describe('ComposerService', () => {
  let service: ComposerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ComposerService, provideHttpClient()]
    });
    service = TestBed.inject(ComposerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
