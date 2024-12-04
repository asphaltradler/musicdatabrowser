import {Component} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {Track} from '../../entities/track';
import {EntityComponent} from './entity.component';

@Component({
  selector: '[app-track-row]',
  standalone: true,
  imports: [NgForOf, NgIf],
  templateUrl: './track.component.html',
})
export class TrackComponent extends EntityComponent<Track> {
}
