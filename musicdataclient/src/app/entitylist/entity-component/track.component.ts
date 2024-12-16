import {Component} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {Track} from '../../entities/track';
import {EntityComponent} from './entity.component';

@Component({
  selector: 'tr.app-entity-row',
  standalone: true,
  imports: [NgForOf, NgIf],
  templateUrl: './track.component.html',
  styleUrls: ['../entity-list.component.css', 'track.component.css']
})
export class TrackComponent extends EntityComponent<Track> {
  getBookletUrl() {
    if (this.entity.bookletId) {
      return this.entityList.service.getDocumentUrl(this.entity.bookletId.valueOf());
    }
    return "";
  }
}
