import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DetailsPopupComponent} from './details.popup.component';

describe('DetailsComponent', () => {
  let component: DetailsPopupComponent;
  let fixture: ComponentFixture<DetailsPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailsPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
