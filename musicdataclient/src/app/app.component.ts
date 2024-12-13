import {Component} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {routes} from './app.routes';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgForOf, NgIf],
  templateUrl: './app.component.html',
  styleUrls: [
    './app.component.css'
    ]
})
export class AppComponent {
  title: string;

  constructor() {
    this.title = 'Musikdatabrowser Client';
  }

  protected readonly routes = routes;
}
