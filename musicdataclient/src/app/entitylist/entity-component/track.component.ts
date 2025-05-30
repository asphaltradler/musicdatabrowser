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
/**
 * Component for displaying and interacting with a single Track entity.
 * 
 * Extends the generic `EntityComponent` to provide additional functionality specific to tracks,
 * such as retrieving the URL for the associated booklet document.
 *
 * @extends EntityComponent<Track>
 */
export class TrackComponent extends EntityComponent<Track> {
  getBookletUrl() {
    if (this.entity.bookletId) {
      return this.entityList().service.getDocumentUrl(this.entity.bookletId.valueOf());
    }
    return "";
  }
}
