import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ComposerListComponent} from './composer-list.component';
import {provideHttpClient} from '@angular/common/http';
import {ComposerService} from '../services/composer.service';
import {provideRouter} from '@angular/router';

describe('ComposerListComponent', () => {
  let component: ComposerListComponent;
  let fixture: ComponentFixture<ComposerListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComposerListComponent],
      providers: [
        ComposerService,
        provideHttpClient(),
        provideRouter([])
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComposerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
