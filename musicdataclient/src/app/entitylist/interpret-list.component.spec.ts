import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {InterpretListComponent} from './interpret-list.component';
import {provideRouter} from '@angular/router';
import {InterpretService} from '../services/interpret.service';

describe('InterpretListComponent', () => {
  let component: InterpretListComponent;
  let fixture: ComponentFixture<InterpretListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InterpretListComponent],
      providers: [
        InterpretService,
        provideHttpClient(),
        provideRouter([])
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterpretListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
