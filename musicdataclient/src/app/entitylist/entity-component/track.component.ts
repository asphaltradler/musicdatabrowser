import {Component} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {Track} from '../../entities/track';
import {EntityComponent} from './entity.component';

@Component({
  selector: 'tr.app-track-row',
  standalone: true,
  imports: [NgForOf, NgIf],
  templateUrl: './track.component.html',
  styleUrls: ['../entity-list.component.css'],
})
export class TrackComponent extends EntityComponent<Track> {
  getAlbumartUrl() {
    if (this.entity.albumart) {
      return this.entityList.service.getDocumentUrl(this.entity.albumart.id);
    }
    return "";
  }
}
