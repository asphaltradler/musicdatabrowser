import {ComponentFixture, TestBed} from '@angular/core/testing';

import {InterpretListComponent} from './interpret-list.component';

describe('InterpretListComponent', () => {
  let component: InterpretListComponent;
  let fixture: ComponentFixture<InterpretListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InterpretListComponent]
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
