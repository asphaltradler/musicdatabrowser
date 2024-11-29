import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {appDefaults} from '../../config/config';

@Component({
  //selector: 'app-status',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './status.component.html',
  styleUrl: './status.component.css'
})
export class StatusComponent implements OnInit {
  private readonly url: string;
  status?: string

  constructor(private http: HttpClient) {
    this.url = appDefaults.serverUrl;
  }

  ngOnInit() {
    const obs =  this.http.get(this.url, {responseType: 'text'});
    obs.subscribe(data => {
      this.status = data;
    });
  }
}
