import {EntityListComponent} from './entity-list.component';
import {AbstractEntity} from '../entities/abstractEntity';
import {ActivatedRoute, Router} from '@angular/router';
import {EntityService} from '../services/entity.service';
import {Title} from '@angular/platform-browser';
import {Subject, Subscription} from 'rxjs';
import {Page} from '../entities/page';
import {Album} from '../entities/album';
import {Track} from '../entities/track';

// Mocks and stubs
class MockEntity extends AbstractEntity {
    static override entityName = 'mock';
    static override namePlural = 'Mocks';
    static override getNameSingular() { return 'Mock'; }
    static override getNumberDescription(n: number) { return `${n} Mock(s)`; }
    static override getNumbersDescription(a: number, b: number) { return `${a}-${b} Mock(s)`; }
    override id: number = 1;
    override name: string = 'MockName';
}

const mockParams = new Subject<any>();
const mockEvents = new Subject<any>();
const mockActivatedRoute = {
    params: mockParams.asObservable(),
    snapshot: {
        params: {},
        data: [MockEntity],
    }
} as any as ActivatedRoute;

const mockRouter = {
    events: mockEvents.asObservable(),
    getCurrentNavigation: () => ({
        previousNavigation: undefined,
        extras: { state: {} }
    }),
    navigate: jest.fn()
} as any as Router;

const mockTitleService = {
    setTitle: jest.fn()
} as any as Title;

const mockEntityService = {
    findByOtherId: jest.fn(),
    findNameLike: jest.fn(),
    findByOtherNameLike: jest.fn(),
    removeById: jest.fn()
} as any as EntityService;

// Mock for viewChild.required
jest.mock('@angular/core', () => {
    const actual = jest.requireActual('@angular/core');
    return {
        ...actual,
        viewChild: {
            required: jest.fn(() => jest.fn(() => ({
                open: jest.fn(),
                searchEntity: MockEntity
            })))
        }
    };
});

// Utility for creating a Page
function createMockPage(content: any[] = [], number = 0, size = 10, totalElements = 10, totalPages = 1, first = true, last = true): Page<any> {
    return {
        content,
        number,
        size,
        totalElements,
        totalPages,
        first,
        last,
        numberOfElements: content.length,
        empty: false,
    };
}

describe('EntityListComponent', () => {
    let component: EntityListComponent<MockEntity>;

    beforeEach(() => {
        jest.clearAllMocks();
        component = new EntityListComponent(
            mockActivatedRoute,
            mockRouter,
            mockTitleService,
            mockEntityService
        );
        // Patch viewChild.required mocks
        (component as any).popup = jest.fn(() => ({ open: jest.fn() }));
        (component as any).searchFieldComponent = jest.fn(() => ({ searchEntity: MockEntity }));
        // Patch entityType and searchEntityType for tests
        component.entityType = MockEntity;
        component.searchEntityType = MockEntity;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should unsubscribe on destroy', () => {
        const unsub1 = jest.spyOn(component['routeChangeSubscription'], 'unsubscribe');
        component['lastSearchSubscription'] = { unsubscribe: jest.fn() } as any as Subscription;
        const unsub2 = jest.spyOn(component['lastSearchSubscription'], 'unsubscribe');
        component.ngOnDestroy();
        expect(unsub1).toHaveBeenCalled();
        expect(unsub2).toHaveBeenCalled();
    });

    it('should call openPopup', () => {
        const entity = new MockEntity();
        const event = { pageX: 10, pageY: 20, offsetY: 5 } as MouseEvent;
        const openSpy = jest.fn();
        (component as any).popup = jest.fn(() => ({ open: openSpy }));
        component.openPopup(entity, event);
        expect(openSpy).toHaveBeenCalledWith(entity.name, '', { x: 20, y: 25 });
    });

    it('should set filterText and lower case', () => {
        component.filterText = 'TEST';
        expect(component['_filterText']).toBe('test');
    });

    it('should set and get pageSize', () => {
        component.searchByEntityName = jest.fn();
        component.pageSize = 42;
        expect(component.pageSize).toBe(42);
        expect(component.searchByEntityName).toHaveBeenCalled();
    });

    it('should set and get searchText', () => {
        component.searchByEntityName = jest.fn();
        component.searchText = 'abc';
        expect(component.searchText).toBe('abc');
        expect(component.searchByEntityName).toHaveBeenCalled();
    });

    it('should call navigateToRoot if paramId is present', () => {
        component['route'].snapshot.params = { id: 1 };
        component.navigateToRoot = jest.fn();
        component.searchText = 'abc';
        expect(component.navigateToRoot).toHaveBeenCalledWith('abc');
    });

    it('should return correct listTitle', () => {
        component.page = createMockPage([new MockEntity(), new MockEntity()], 0, 2, 2, 1, true, true);
        component['titleFor'] = 'foo';
        expect(component.listTitle).toContain('2 Mock(s) foo');
    });

    it('should filter entities', () => {
        component['page'] = createMockPage([
            { id: 1, name: 'foo' },
            { id: 2, name: 'bar' }
        ], 0, 2, 2, 1, true, true);
        component.filterText = 'foo';
        expect(component.isEntityFiltered({ name: 'foo' } as any)).toBe(false);
        expect(component.isEntityFiltered({ name: 'bar' } as any)).toBe(true);
        expect(component.isEntityShown({ name: 'foo' } as any)).toBe(true);
        expect(component.isEntityShown({ name: 'bar' } as any)).toBe(false);
    });

    it('should return hasPreviousPage/hasNextPage', () => {
        component.page = createMockPage([], 0, 10, 10, 1, true, false);
        expect(component.hasPreviousPage()).toBe(false);
        expect(component.hasNextPage()).toBe(true);
        component.page = createMockPage([], 0, 10, 10, 1, false, true);
        expect(component.hasPreviousPage()).toBe(true);
        expect(component.hasNextPage()).toBe(false);
    });

    it('should call removeEntity and reload page on success', () => {
        const entity = new MockEntity();
        const subscribeMock = jest.fn(({ next }: any) => next());
        component.loadPage = jest.fn();
        (component.service.removeById as jest.Mock).mockReturnValue({ subscribe: subscribeMock });
        component.removeEntity(entity);
        expect(component.service.removeById).toHaveBeenCalledWith(MockEntity, entity.id);
        expect(component.loadPage).toHaveBeenCalled();
    });

    it('should call removeEntity and log error on error', () => {
        const entity = new MockEntity();
        const subscribeMock = jest.fn(({ error }: any) => error('fail'));
        (component.service.removeById as jest.Mock).mockReturnValue({ subscribe: subscribeMock });
        component.removeEntity(entity);
        expect(component.service.removeById).toHaveBeenCalledWith(MockEntity, entity.id);
    });

    it('should get correct table header and row components', () => {
        component.entityType = Album;
        expect(component.getTableHeaderComponent().name).toContain('AlbumTableHeaderComponent');
        expect(component.getTableRowComponent().name).toContain('AlbumComponent');
        component.entityType = Track;
        expect(component.getTableHeaderComponent().name).toContain('TrackTableHeaderComponent');
        expect(component.getTableRowComponent().name).toContain('TrackComponent');
        component.entityType = MockEntity;
        expect(component.getTableHeaderComponent().name).toContain('TableHeaderComponent');
        expect(component.getTableRowComponent().name).toContain('EntityComponent');
    });

    it('should call searchPreviousPage and searchNextPage', () => {
        component.loadPage = jest.fn();
        component.searchPreviousPage();
        expect(component.loadPage).toHaveBeenCalledWith(-1);
        component.searchNextPage();
        expect(component.loadPage).toHaveBeenCalledWith(1);
    });

    it('should call loadPage and searchByEntityIdOrName', () => {
        component.searchEntityType = MockEntity;
        component.page = createMockPage([], 1, 10, 10, 2, false, false);
        component['lastSearchId'] = undefined;
        component['_searchText'] = '';
        component['searchByEntityIdOrName'] = jest.fn();
        component.loadPage(1);
        expect(component['searchByEntityIdOrName']).toHaveBeenCalled();
    });

    it('should call navigateOtherEntityByThis', () => {
        component.navigateOtherEntityBy = jest.fn();
        const entity = new MockEntity();
        component.navigateOtherEntityByThis(MockEntity, entity);
        expect(component.navigateOtherEntityBy).toHaveBeenCalledWith(MockEntity, MockEntity, entity, entity);
    });

    it('should call navigateOtherEntityByItself', () => {
        component.navigateOtherEntityBy = jest.fn();
        const entity = new MockEntity();
        component.navigateOtherEntityByItself(MockEntity, entity, entity);
        expect(component.navigateOtherEntityBy).toHaveBeenCalledWith(MockEntity, MockEntity, entity, entity);
    });

    it('should call navigateOtherEntityBy and router.navigate', () => {
        const entity = new MockEntity();
        component.storeState = jest.fn(() => ({ foo: 'bar' }));
        component.router = { navigate: jest.fn() } as any;
        component.navigateOtherEntityBy(MockEntity, MockEntity, entity, entity);
        expect(component.router.navigate).toHaveBeenCalled();
    });

    it('should call navigateToRoot and router.navigate', () => {
        component.storeState = jest.fn(() => ({ foo: 'bar' }));
        component.router = { navigate: jest.fn() } as any;
        component.navigateToRoot('abc');
        expect(component.router.navigate).toHaveBeenCalled();
    });

    it('should call navigateToDetails and router.navigate', () => {
        const entity = new MockEntity();
        component.storeState = jest.fn(() => ({ foo: 'bar' }));
        component.router = { navigate: jest.fn() } as any;
        component.navigateToDetails(entity);
        expect(component.router.navigate).toHaveBeenCalled();
    });

    it('should store state with correct params', () => {
        component.page = createMockPage([], 2, 10, 10, 1, false, false);
        component.pageSize = 10;
        component['searchText'] = 'foo';
        component['filterText'] = 'bar';
        const state = component['storeState'](new MockEntity(), new MockEntity());
        expect(state.hasOwnProperty('entity')).toBeTrue();
        expect(state.hasOwnProperty('sourceEntity')).toBeTrue();
        expect(state.hasOwnProperty('searchEntity')).toBeTrue();
        expect(state.hasOwnProperty('searchText')).toBeTrue();
        expect(state.hasOwnProperty('filterText')).toBeTrue();
        expect(state.hasOwnProperty('pageNumber')).toBeTrue();
        expect(state.hasOwnProperty('pageSize')).toBeTrue();
    });

    it('should getEntities return page content', () => {
        const entities = [new MockEntity(), new MockEntity()];
        component.page = createMockPage(entities);
        expect(component.getEntities()).toEqual(entities);
    });

    it('should getEntityId return entity id', () => {
        const entity = { id: 123 } as AbstractEntity;
        expect(component.getEntityId(0, entity)).toBe(123);
    });

    it('should get searchableEntities', () => {
        component['entityType'] = MockEntity;
        jest.spyOn(component, 'searchableEntities', 'get').mockReturnValue([MockEntity]);
        expect(component.searchableEntities).toEqual([MockEntity]);
    });
});
