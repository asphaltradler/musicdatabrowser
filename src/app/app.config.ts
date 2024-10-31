import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
//Das ist wichtig!
import {provideHttpClient} from '@angular/common/http';

import {routes} from './app.routes';
import {SearchfieldComponent} from './search/searchfield.component';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}),
    provideHttpClient(),
    provideRouter(routes),
    SearchfieldComponent
  ],
};
