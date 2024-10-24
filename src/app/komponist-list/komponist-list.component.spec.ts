import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KomponistListComponent } from './komponist-list.component';

describe('KomponistListComponent', () => {
  let component: KomponistListComponent;
  let fixture: ComponentFixture<KomponistListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KomponistListComponent]
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
