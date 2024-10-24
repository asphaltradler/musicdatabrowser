import {Component, OnInit} from '@angular/core';
import {Album} from '../entities/album';
import {AlbumService} from '../services/album.service';
import {Komponist} from '../entities/komponist';
import {KomponistService} from '../services/komponist.service';

@Component({
  selector: 'app-komponist-list',
  standalone: true,
  imports: [],
  templateUrl: './komponist-list.component.html',
  styleUrl: './komponist-list.component.css'
})
export class KomponistListComponent implements OnInit {

  komponisten: Komponist[] | undefined;

  constructor(private komponistService: KomponistService) {
  }

  ngOnInit() {
    this.komponistService.findAll().subscribe(data => {
      this.komponisten = data;
    });
  }

}
