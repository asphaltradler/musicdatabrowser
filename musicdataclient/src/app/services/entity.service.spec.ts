import {TestBed} from '@angular/core/testing';
import {EntityService} from './entity.service';
import {HttpClient} from '@angular/common/http';
import {of} from 'rxjs';
import {AbstractEntity} from '../entities/abstractEntity';
import {Page} from '../entities/page';

class TestEntity extends AbstractEntity {
    static override entityName = 'testentity';
}

class OtherEntity extends AbstractEntity {
    static override entityName = 'otherentity';
}

describe('EntityService', () => {
    let service: EntityService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'delete']);
        TestBed.configureTestingModule({
            providers: [
                EntityService,
                { provide: HttpClient, useValue: httpClientSpy }
            ]
        });
        service = TestBed.inject(EntityService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('findByOtherNameLike', () => {
        it('should call httpClient.get with correct url and params', () => {
            const expectedPage: Page<TestEntity> = {
                content: [], totalElements: 0, totalPages: 0, number: 0, size: 10,
                empty: false,
                first: false,
                last: false,
                numberOfElements: 0
            };
            httpClientSpy.get.and.returnValue(of(expectedPage));
            service.findByOtherNameLike(TestEntity, OtherEntity, 'search', 1, 5).subscribe(result => {
                expect(result).toEqual(expectedPage);
            });
            const url = service.findPageByUrl.replace('{}', TestEntity.entityName);
            expect(httpClientSpy.get).toHaveBeenCalledWith(jasmine.stringMatching(url), jasmine.any(Object));
        });
    });

    describe('findNameLike', () => {
        it('should call httpClient.get with correct url and params', () => {
            const expectedPage: Page<TestEntity> = {
                content: [], totalElements: 0, totalPages: 0, number: 0, size: 10,
                empty: false,
                first: false,
                last: false,
                numberOfElements: 0
            };
            httpClientSpy.get.and.returnValue(of(expectedPage));
            service.findNameLike(TestEntity, 'search', 2, 15).subscribe(result => {
                expect(result).toEqual(expectedPage);
            });
            const url = service.findPageUrl.replace('{}', TestEntity.entityName);
            expect(httpClientSpy.get).toHaveBeenCalledWith(jasmine.stringMatching(url), jasmine.any(Object));
        });
    });

    describe('findByOtherId', () => {
        it('should call httpClient.get with correct url and params', () => {
            const expectedPage: Page<TestEntity> = {
                content: [], totalElements: 0, totalPages: 0, number: 0, size: 10,
                empty: false,
                first: false,
                last: false,
                numberOfElements: 0
            };
            httpClientSpy.get.and.returnValue(of(expectedPage));
            service.findByOtherId(TestEntity, OtherEntity, 42, 3, 20).subscribe(result => {
                expect(result).toEqual(expectedPage);
            });
            const url = service.getPageUrl.replace('{}', TestEntity.entityName);
            expect(httpClientSpy.get).toHaveBeenCalledWith(jasmine.stringMatching(url), jasmine.any(Object));
        });
    });

    describe('getById', () => {
        it('should call httpClient.get with correct url', () => {
            const entity = { id: 1 } as TestEntity;
            httpClientSpy.get.and.returnValue(of(entity));
            service.getById(TestEntity, 1).subscribe(result => {
                expect(result).toEqual(entity);
            });
            const url = service.getByIdUrl.replace('{}', TestEntity.entityName).replace('{id}', '1');
            expect(httpClientSpy.get).toHaveBeenCalledWith(url);
        });
    });

    describe('removeById', () => {
        it('should call httpClient.delete with correct url', () => {
            httpClientSpy.delete.and.returnValue(of({}));
            service.removeById(TestEntity, 99).subscribe();
            const url = service.removeByIdUrl.replace('{}', TestEntity.entityName).replace('{id}', '99');
            expect(httpClientSpy.delete).toHaveBeenCalledWith(url);
        });
    });

    describe('findDocumentById', () => {
        it('should call httpClient.get with document url', () => {
            const fakeImageBitmap = {} as ImageBitmap;
            httpClientSpy.get.and.returnValue(of(fakeImageBitmap));
            service.findDocumentById(123).subscribe(result => {
                expect(result).toBe(fakeImageBitmap);
            });
            expect(httpClientSpy.get).toHaveBeenCalledWith(service.getDocumentUrl(123));
        });
    });

    describe('getDocumentUrl', () => {
        it('should return correct document url', () => {
            expect(service.getDocumentUrl(5)).toBe(service.documentUrl + '5');
        });
    });

    describe('getThumbnailUrl', () => {
        it('should return correct thumbnail url', () => {
            expect(service.getThumbnailUrl(7)).toBe(service.thumbnailUrl + '7');
        });
    });
});
