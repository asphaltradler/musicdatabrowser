import {Component} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {Album} from './entities/album';
import {Komponist} from './entities/komponist';
import {Werk} from './entities/werk';
import {Interpret} from './entities/interpret';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: [
    './app.component.css'
    ]
})
export class AppComponent {
  title: string;

  constructor() {
    this.title = 'Musikserver Client';
  }

  protected readonly Album = Album;
  protected readonly Komponist = Komponist;
  protected readonly Werk = Werk;
  protected readonly Interpret = Interpret;
}
