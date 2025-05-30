import {ComponentFixture, TestBed} from '@angular/core/testing';
import {EntityComponent} from './entity.component';
import {ElementRef} from '@angular/core';
import {Router} from '@angular/router';
import {AbstractEntity} from '../../entities/abstractEntity';
import {EntityListComponent} from '../entity-list.component';
import {EntityService} from '../../services/entity.service';
import {HttpClient} from '@angular/common/http';

// Mock classes
class MockEntity extends AbstractEntity {
    override id = 1;
}


class MockService extends EntityService {
    override baseUrl = '';
    override findPageUrl = '';
    override findPageByUrl = '';
    override getPageUrl = '';
    override getById = jasmine.createSpy('getById');
    override findByOtherId = jasmine.createSpy('findByOtherId');
    override getThumbnailUrl = jasmine.createSpy('getThumbnailUrl').and.returnValue('thumb-url');
    findPage = jasmine.createSpy('findPage');
    findPageBy = jasmine.createSpy('findPageBy');
    getAll = jasmine.createSpy('getAll');
    getAllBy = jasmine.createSpy('getAllBy');
    getAllByOtherId = jasmine.createSpy('getAllByOtherId');
    getAllByOtherIds = jasmine.createSpy('getAllByOtherIds');
    getAllByIds = jasmine.createSpy('getAllByIds');
    getAllByString = jasmine.createSpy('getAllByString');
    getAllByStrings = jasmine.createSpy('getAllByStrings');
    getAllByStringAndOtherId = jasmine.createSpy('getAllByStringAndOtherId');
    getAllByStringAndOtherIds = jasmine.createSpy('getAllByStringAndOtherIds');
    getAllByStringAndIds = jasmine.createSpy('getAllByStringAndIds');
    getAllByStringAndStrings = jasmine.createSpy('getAllByStringAndStrings');
}

class MockEntityListComponent extends EntityListComponent<MockEntity> {
    override isEntityFiltered = jasmine.createSpy('isEntityFiltered').and.returnValue(false);
    override navigateToDetails = jasmine.createSpy('navigateToDetails');

    constructor() {
        // Provide a mock HttpClient (can be a jasmine spy object)
        const httpClientSpy = jasmine.createSpyObj<HttpClient>('HttpClient', ['get', 'post', 'put', 'delete']);
        // Provide mock values for all required constructor arguments
        const mockRouter = jasmine.createSpyObj('Router', ['navigate']);
        const mockTitle = jasmine.createSpyObj('Title', ['setTitle']);
        const mockActivatedRoute = jasmine.createSpyObj('ActivatedRoute', [], { snapshot: {} });
        super(mockRouter, mockTitle, mockActivatedRoute, new MockService(httpClientSpy));
    }
}

describe('EntityComponent', () => {
    let component: EntityComponent<MockEntity>;
    let fixture: ComponentFixture<EntityComponent<MockEntity>>;
    let hostElement: ElementRef<HTMLElement>;
    let router: Router;
    let entityList: MockEntityListComponent;
    let nativeElement: HTMLElement;

    beforeEach(() => {
        nativeElement = document.createElement('tr');
        hostElement = new ElementRef(nativeElement);
        router = jasmine.createSpyObj('Router', ['navigate']);
        entityList = new MockEntityListComponent();

        TestBed.configureTestingModule({
            providers: [
                { provide: ElementRef, useValue: hostElement },
                { provide: Router, useValue: router }
            ]
        });

        component = new EntityComponent(hostElement, router);
    });

    it('should set entity and update id attribute', () => {
        const entity = new MockEntity();
        component.entity = entity;
        expect(component._entity).toBe(entity);
        expect(nativeElement.getAttribute('id')).toBe('mockentity-1');
    });

    it('should set hidden attribute when hidden is true', () => {
        component.hidden = true;
        expect(nativeElement.hasAttribute('hidden')).toBeTrue();
    });

    it('should remove hidden attribute when hidden is false', () => {
        nativeElement.setAttribute('hidden', '');
        component.hidden = false;
        expect(nativeElement.hasAttribute('hidden')).toBeFalse();
    });

    it('should call isEntityFiltered when setting entity', () => {
        const entity = new MockEntity();
        spyOnProperty(component, 'entityList', 'get').and.returnValue(entityList);
        component.entity = entity;
        expect(entityList.isEntityFiltered).toHaveBeenCalledWith(entity);
    });

    it('should get other entities by this id', () => {
        const entity = new MockEntity();
        component.entity = entity;
        component.getOtherEntitiesByThisId(AbstractEntity);
        expect(entityList.service.findByOtherId).toHaveBeenCalledWith(
            AbstractEntity,
            entityList.entityType,
            entity.id
        );
    });

    it('should return albumart url if albumartId is present', () => {
        const entity = new MockEntity();
        entity.albumartId = 42;
        component.entity = entity;
        const url = component.getAlbumartUrl();
        expect(entityList.service.getThumbnailUrl).toHaveBeenCalledWith(42);
        expect(url).toBe('thumb-url');
    });

    it('should return empty string if albumartId is not present', () => {
        const entity = new MockEntity();
        component.entity = entity;
        const url = component.getAlbumartUrl();
        expect(url).toBe('');
    });

    it('should navigate to details', () => {
        const entity = new MockEntity();
        component.entity = entity;
        component.navigateToDetails();
        expect(entityList.navigateToDetails).toHaveBeenCalledWith(entity);
    });

    it('getIdForEntity should return correct id string', () => {
        const entity = new MockEntity();
        const id = EntityComponent.getIdForEntity(entityList, entity);
        expect(id).toBe('mockentity-1');
    });
});
