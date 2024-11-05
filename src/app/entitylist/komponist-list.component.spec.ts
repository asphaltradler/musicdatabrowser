import {ComponentFixture, TestBed} from '@angular/core/testing';

import {KomponistListComponent} from './komponist-list.component';
import {provideHttpClient} from '@angular/common/http';
import {KomponistService} from '../services/komponist.service';
import {provideRouter} from '@angular/router';

describe('KomponistListComponent', () => {
  let component: KomponistListComponent;
  let fixture: ComponentFixture<KomponistListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KomponistListComponent],
      providers: [
        KomponistService,
        provideHttpClient(),
        provideRouter([])
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(KomponistListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
