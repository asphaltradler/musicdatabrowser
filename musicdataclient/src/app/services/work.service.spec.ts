import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {WorkService} from './work.service';
import {Work} from '../entities/work';

describe('WorkService', () => {
  let service: WorkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Work, provideHttpClient()]
    });
    service = TestBed.inject(WorkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
