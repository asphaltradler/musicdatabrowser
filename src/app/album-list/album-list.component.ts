import {Component, OnInit} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchlistComponent} from '../searchlist/searchlist.component';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SearchlistComponent
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent implements OnInit {
  alben: Album[] = [];
  title: string = "Alben";

  constructor(private albumService: AlbumService) {
  }

  public search(searchText: string) {
    console.log("Suche ALBUM nach " + searchText);
    this.albumService.find(searchText).subscribe(data => {
      this.title = "Alle Alben" + (searchText ? " für " + searchText : "");
      this.alben = data;
      for (const alb of this.alben) {
        alb.track_url = "http://localhost:8080/musik/track/get?album-id=" + alb.id;
      }
    });
  }

  getTitle() {
    return this.title;
  }

  ngOnInit(): void {
    this.search('');
  }

}
