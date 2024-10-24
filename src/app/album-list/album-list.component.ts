import {Component, OnInit} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.css'
})
export class AlbumListComponent implements OnInit {

  alben: Album[] | undefined;

  constructor(private albumService: AlbumService) {
  }

  ngOnInit() {
    this.albumService.find("beeth").subscribe(data => {
      this.alben = data;
    });
  }

}
