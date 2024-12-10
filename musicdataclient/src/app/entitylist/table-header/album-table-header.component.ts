import {Component} from '@angular/core';
import {NgForOf} from '@angular/common';
import {TableHeaderComponent} from './table-header.component';
import {Track} from '../../entities/track';

@Component({
  selector: 'tr.app-album-table-header',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './album-table-header.component.html',
  styles: '.text-right {text-align: right;}'
})
export class AlbumTableHeaderComponent extends TableHeaderComponent<Track> {
}
