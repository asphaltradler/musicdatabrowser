import {TrackComponent} from './track.component';
import {Track} from '../../entities/track';
import {Router} from '@angular/router';
import {ElementRef} from '@angular/core';

describe('TrackComponent', () => {
    let component: TrackComponent;
    let mockEntityList: any;
    let mockService: any;

    beforeEach(() => {
        mockService = {
            getDocumentUrl: jasmine.createSpy('getDocumentUrl').and.returnValue('http://example.com/booklet.pdf')
        };
        mockEntityList = jasmine.createSpy('entityList').and.returnValue({ service: mockService });

        const mockElementRef = { nativeElement: document.createElement('tr') } as ElementRef<HTMLElement>;
        const mockRouter = {} as Router;
        component = new TrackComponent(mockElementRef, mockRouter);
        // @ts-ignore
        component.entityList = mockEntityList;
        // @ts-ignore
        component.entity = {} as Track;
    });

    it('should return booklet URL when bookletId is present', () => {
        // @ts-ignore
        component.entity.bookletId = 123;
        const url = component.getBookletUrl();
        expect(mockService.getDocumentUrl).toHaveBeenCalledWith(123);
        expect(url).toBe('http://example.com/booklet.pdf');
    });

    it('should return empty string when bookletId is not present', () => {
        // @ts-ignore
        component.entity.bookletId = undefined;
        const url = component.getBookletUrl();
        expect(url).toBe('');
        expect(mockService.getDocumentUrl).not.toHaveBeenCalled();
    });
});
