import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {Album} from '../../entities/album';
import {AlbumListComponent} from '../album-list.component';
import {Track} from '../../entities/track';
import {NgForOf} from '@angular/common';
import {AlbumModel} from '../../entitymodels/album-model';

@Component({
  selector: '[app-album-row]',
  standalone: true,
  imports: [NgForOf],
  templateUrl: './album.component.html',
})
export class AlbumComponent implements OnChanges {
  @Input({required: true}) albumModel!: AlbumModel;
  @Input({required: true}) albumList!: AlbumListComponent;

  ngOnChanges(changes:SimpleChanges) {
    console.log(changes);
    const modelChange = changes['albumModel'];
    const prevModel: AlbumModel = modelChange?.previousValue;
    const newModel: AlbumModel = modelChange?.currentValue;
    //gleiche Entities ignorieren
    if (newModel?.entity !== prevModel?.entity) {
      this.albumModel.lazyLoad(
        this.albumList.composersService, this.albumList.artistsService,
        this.albumList.workService, this.albumList.genreService);
    }
  }

  protected readonly Track = Track;
  protected readonly Album = Album;
}
