import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-search-album',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './status.component.html',
  styleUrl: './status.component.css'
})
export class StatusComponent implements OnInit {

  private url: string;
  private status: string | undefined

  constructor(private http: HttpClient) {
    this.url = 'http://localhost:8080/musik';
  }

  ngOnInit() {
    const obs =  this.http.get(this.url, {responseType: 'text'});
    obs.subscribe(data => {
      this.status = data;
    });
  }

  getStatus() {
    return this.status;
  }
}
