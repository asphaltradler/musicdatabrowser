import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DetailsPopupComponent} from './details.popup.component';
import {Album} from '../../entities/album';

describe('DetailsComponent', () => {
  let component: DetailsPopupComponent<Album>;
  let fixture: ComponentFixture<DetailsPopupComponent<Album>>;

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

  it('should open the popup with correct values and set style', () => {
    const setAttributeSpy = jasmine.createSpy('setAttribute');
    const mockElement = { setAttribute: setAttributeSpy } as any;
    spyOn(document, 'getElementById').and.returnValue(mockElement);

    component.open('Test Title', 'Test Message', { x: 100, y: 200 });

    expect(component.isOpen()).toBeTrue();
    expect(component.message()).toBe('Test Message');
    expect(component.title).toBe('Test Title');
    expect(document.getElementById).toHaveBeenCalledWith('details-popup');
    expect(setAttributeSpy).toHaveBeenCalledWith('style', 'left:100px;top:200px');
  });

  it('should close the popup and emit closed event', () => {
    spyOn(component.closed, 'emit');
    component.isOpen.set(true);

    component.close();

    expect(component.isOpen()).toBeFalse();
    expect(component.closed.emit).toHaveBeenCalled();
  });
});
