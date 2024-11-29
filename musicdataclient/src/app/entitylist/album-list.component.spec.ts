import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AlbumListComponent} from './album-list.component';
import {provideHttpClient} from '@angular/common/http';
import {AlbumService} from '../services/album.service';
import {provideRouter} from '@angular/router';

describe('AlbumListComponent', () => {
  let component: AlbumListComponent;
  let fixture: ComponentFixture<AlbumListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumListComponent],
      providers: [
        AlbumService,
        provideRouter([]),
        provideHttpClient()
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlbumListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
