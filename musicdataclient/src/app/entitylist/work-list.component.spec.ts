import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkListComponent} from './work-list.component';
import {provideHttpClient} from '@angular/common/http';
import {WorkService} from '../services/work.service';
import {provideRouter} from '@angular/router';

describe('WorkListComponent', () => {
  let component: WorkListComponent;
  let fixture: ComponentFixture<WorkListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkListComponent],
      providers: [
        WorkService,
        provideHttpClient(),
        provideRouter([])
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
