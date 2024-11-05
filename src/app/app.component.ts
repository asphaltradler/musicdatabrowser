import {Component} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {routes} from './app.routes';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
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

  protected readonly routes = routes;
}
