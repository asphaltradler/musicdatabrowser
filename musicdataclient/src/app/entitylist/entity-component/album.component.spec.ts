import {AlbumComponent} from './album.component';
import {ChangeDetectorRef, ElementRef} from '@angular/core';
import {Router} from '@angular/router';
import {of} from 'rxjs';
import {Composer} from '../../entities/composer';
import {Work} from '../../entities/work';
import {Genre} from '../../entities/genre';
import {Artist} from '../../entities/artist';
import {AbstractEntity} from '../../entities/abstractEntity';

describe('AlbumComponent', () => {
    let component: AlbumComponent;
    let mockElementRef: ElementRef;
    let mockRouter: Router;
    let mockChangeRef: ChangeDetectorRef;

    beforeEach(() => {
        mockElementRef = { nativeElement: document.createElement('tr') } as ElementRef;
        mockRouter = {} as Router;
        mockChangeRef = { markForCheck: jasmine.createSpy('markForCheck') } as any;

        // Mock IntersectionObserver
        (window as any).IntersectionObserver = class {
            observe = jasmine.createSpy('observe');
            disconnect = jasmine.createSpy('disconnect');
            constructor(public cb: any) {}
        };

        component = new AlbumComponent(mockElementRef, mockRouter, mockChangeRef);
        // Mock entity and entityList
        (component as any).entity = {
            name: 'Test Album',
            bookletId: 123,
            bookletName: 'folder/booklet.pdf'
        };
        (component as any).entityList = {
            service: {
                getDocumentUrl: jasmine.createSpy('getDocumentUrl').and.returnValue('http://test/doc/123')
            }
        };
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    describe('getBookletUrl', () => {
        it('should return document url if bookletId exists', () => {
            expect(component.getBookletUrl()).toBe('http://test/doc/123');
            expect(component.entityList().service.getDocumentUrl).toHaveBeenCalledWith(123);
        });

        it('should return empty string if no bookletId', () => {
            (component as any).entity.bookletId = undefined;
            expect(component.getBookletUrl()).toBe('');
        });
    });

    describe('getBookletName', () => {
        it('should strip folder and return name', () => {
            expect(component.getBookletName()).toBe('booklet.pdf');
        });

        it('should truncate long names', () => {
            (component as any).entity.bookletName = 'folder/averyveryverylongbookletfilename.pdf';
            const name = component.getBookletName(10);
            expect(name?.startsWith('...')).toBeTrue();
            expect(name?.length).toBeLessThanOrEqual(10);
        });

        it('should return undefined if no bookletName', () => {
            (component as any).entity.bookletName = undefined;
            expect(component.getBookletName()).toBeUndefined();
        });
    });

    describe('getSearchEntities', () => {
        it('should return composers', () => {
            component.composers = [{ id: 1 }] as any;
            expect(component.getSearchEntities(Composer)).toBe(component.composers);
        });
        it('should return works', () => {
            component.works = [{ id: 2 }] as any;
            expect(component.getSearchEntities(Work)).toBe(component.works);
        });
        it('should return genres', () => {
            component.genres = [{ id: 3 }] as any;
            expect(component.getSearchEntities(Genre)).toBe(component.genres);
        });
        it('should return artists', () => {
            component.artists = [{ id: 4 }] as any;
            expect(component.getSearchEntities(Artist)).toBe(component.artists);
        });
        it('should return empty array for unknown entity', () => {
            expect(component.getSearchEntities(class extends AbstractEntity {})).toEqual([]);
        });
    });

    describe('lazyLoadLists', () => {
        beforeEach(() => {
            spyOn(component, 'getOtherEntitiesByThisId').and.callFake((entityType: any) => {
                return of({ content: [
                    { id: 1, name: `${entityType.name}1` },
                    { id: 2, name: `${entityType.name}2` }
                ] });
            });
            spyOn(console, 'log');
            component.obs.disconnect = jasmine.createSpy('disconnect');
        });

       it('should load all lists and mark for check', (done) => {
            component.lazyLoadLists();
            setTimeout(() => {
                expect(component.composers).toEqual([
                    { id: 1, name: 'Composer1' },
                    { id: 2, name: 'Composer2' }
                ]);
                expect(component.works).toEqual([
                    { id: 1, name: 'Work1' },
                    { id: 2, name: 'Work2' }
                ]);
                expect(component.genres).toEqual([
                    { id: 1, name: 'Genre1' },
                    { id: 2, name: 'Genre2' }
                ]);
                expect(component.artists).toEqual([
                    { id: 1, name: 'Artist1' },
                    { id: 2, name: 'Artist2' }
                ]);
                 expect(mockChangeRef.markForCheck).toHaveBeenCalled();
                expect(component.obs.disconnect).toHaveBeenCalled();
                done();
            }, 0);
        });
    });
});
