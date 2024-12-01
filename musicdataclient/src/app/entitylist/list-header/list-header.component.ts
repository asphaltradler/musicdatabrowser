import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-list-header',
    standalone: true,
    imports: [],
    templateUrl: './list-header.component.html',
    styleUrl: '../entity-list.component.css'
})
export class ListHeaderComponent {
    @Input() title?: string;
    @Input() lastSearchPerformance?: string;
}
