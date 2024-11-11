import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TrackListComponent} from './track-list.component';
import {provideHttpClient} from '@angular/common/http';
import {provideRouter} from '@angular/router';
import {TrackService} from '../services/track.service';

describe('TrackListComponent', () => {
  let component: TrackListComponent;
  let fixture: ComponentFixture<TrackListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackListComponent],
      providers: [
        TrackService,
        provideHttpClient(),
        provideRouter([])
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
