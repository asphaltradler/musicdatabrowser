import {Component, EventEmitter, Output, signal} from '@angular/core';
import {EntityDetailsComponent} from '../../details/entity-details/entity-details.component';
import {AbstractEntity} from '../../entities/abstractEntity';

@Component({
  selector: 'app-popup',
  standalone: true,
  //TODO Layer und Popup sollten in document.body ganz oben eingefügt werden
  templateUrl: 'details.popup.component.html',
  styleUrl: 'details.popup.component.css',
})
//TODO gemeinsamen Code für Darstellung E auslagern
export class DetailsPopupComponent<E extends AbstractEntity> extends EntityDetailsComponent<E> {
  isOpen = signal(false);
  //title = signal('');
  message = signal('');

  //@Input({required: true}) entity!: E;
  @Output() closed = new EventEmitter<void>();

  open(title: string, message: string, position: { x: number, y: number }) {
    //this.title.set(title);
    this.title = title;
    this.message.set(message);
    this.isOpen.set(true);
    //TODO HTMLElement eleganter zugänglich als einfaches JS?
    const el = document.getElementById('details-popup');
    el?.setAttribute('style', `left:${position.x}px;top:${position.y}px`);
  }

  close() {
    this.isOpen.set(false);
    this.closed.emit();
  }
}
