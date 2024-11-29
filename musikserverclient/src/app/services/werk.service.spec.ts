import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {WerkService} from './werk.service';
import {Werk} from '../entities/werk';

describe('WerkService', () => {
  let service: WerkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Werk, provideHttpClient()]
    });
    service = TestBed.inject(WerkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
