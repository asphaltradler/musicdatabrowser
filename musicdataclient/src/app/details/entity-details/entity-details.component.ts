import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {EntityService} from '../../services/entity.service';
import {NgForOf, NgIf} from '@angular/common';
import {AbstractEntity} from '../../entities/abstractEntity';
import {getEntityForName, paramEntity, paramId} from '../../../config/utilities';

@Component({
  selector: 'app-track-details',
  standalone: true,
  imports: [
    NgIf,
    NgForOf
  ],
  templateUrl: './entity-details.component.html',
  styleUrl: './entity-details.component.css'
})
export class EntityDetailsComponent<E extends AbstractEntity> {
  entity?: E;
  title?: string;

  constructor(route: ActivatedRoute, router: Router, private titleService: Title,
              private service: EntityService) {
    const snapshot = route.snapshot;
    const params = snapshot.params;
    const entityType = getEntityForName(params[paramEntity]);
    const id = params[paramId];

    //Entity in state übergeben?
    const entity: E = router.getCurrentNavigation()?.extras?.state?.[paramEntity];
    if (entity) {
      this.setEntity(entity);
    } //sonst neu holen
    else if (entityType && id) {
      this.service.getById<E>(entityType, id).subscribe(entity => {
        this.setEntity(entity);
      });
    }
  }

  private setEntity = (entity: E) => {
    this.entity = entity;
    this.title = `Details für ${this.entity.name}`;
    console.log(`${this.title} id=${entity.id}`);
    this.titleService.setTitle(this.title);
  }

  getAlbumartUrl() {
    if (this.entity?.albumartId) {
      return this.service.getDocumentUrl(this.entity.albumartId.valueOf());
    }
    return "";
  }

  getAllProperties(): [key: string, value: any][] {
    if (this.entity) {
      return Object.entries(this.entity).filter(this.filterAttributes);
    }
    return [];
  }

  public getAllPropertiesFor(o: any): string {
    if (o) {
      if (Array.isArray(o)) {
        return Object.entries(o).map(([, value]) =>
          this.getAllPropertiesFor(value)).join('; ');
      } else if (typeof o === 'object') {
        return Object.entries(o).filter(this.filterChildAttributes)
          .map(([, value]) =>
            this.getAllPropertiesFor(value)).join('; ');
      } else {
        return o.toString();
      }
    }
    return '';
  }

  private filterAttributes = (e: [string,any]) => {
    return !e[0].includes('albumart') && !e[0].includes('Id');
  }

  private filterChildAttributes = (e: [key: string, val: unknown]) => {
    return !e[0].endsWith('Id') && e[0] !== 'id' && !e[0].includes('albumart');
  }

  protected readonly history = history;
}
