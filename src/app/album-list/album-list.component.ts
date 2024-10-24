import {Component, OnInit} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent implements OnInit {

  alben: Album[] = [];
  filter: string | undefined;

  constructor(private albumService: AlbumService) {
  }

  ngOnInit() {
    this.searchForm.value.searchText = this.filter;
    this.search();
  }

  private search() {
    this.albumService.find(this.filter).subscribe(data => {
      this.alben = data;
      for (const alb of this.alben) {
        alb.track_url = "#" + alb.id;
      }
    });
  }

  searchForm = new FormGroup({
    searchText: new FormControl('')
  });

  getTitle() {
    return "Alle Alben" + (this.filter ? " für " + this.filter : "");
  }

  handleSubmit() {
    this.filter = this.searchForm.value.searchText || '';
    this.search();
  }

}
