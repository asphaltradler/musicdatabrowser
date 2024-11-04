import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WerkListComponent} from './werk-list.component';

describe('WerkListComponent', () => {
  let component: WerkListComponent;
  let fixture: ComponentFixture<WerkListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WerkListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WerkListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
