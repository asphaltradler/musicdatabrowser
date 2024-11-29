import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GenreListComponent} from './genre-list.component';
import {provideHttpClient} from '@angular/common/http';
import {provideRouter} from '@angular/router';
import {GenreService} from '../services/genre.service';

describe('GenreListComponent', () => {
  let component: GenreListComponent;
  let fixture: ComponentFixture<GenreListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenreListComponent],
      providers: [
        GenreService,
        provideHttpClient(),
        provideRouter([])
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenreListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
