import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EntityDetailsComponent} from './entity-details.component';

describe('TrackDetailsComponent', () => {
  let component: EntityDetailsComponent<any>;
  let fixture: ComponentFixture<EntityDetailsComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntityDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
