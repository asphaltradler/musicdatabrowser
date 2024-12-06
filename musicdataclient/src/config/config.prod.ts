import {InjectionToken} from '@angular/core';
import {AbstractEntity} from '../app/entities/abstractEntity';

export const appDefaults = {
  serverUrl: 'http://' + window.location.hostname + ':8080/music/',
  serviceParamSuffixId: 'Id',
  serviceParamName: 'name',
  serviceParamPageNumber: 'pageNumber',
  serviceParamPageSize: 'pageSize',

  pageSizes: [10, 25, 50, 100, 250, 500, 1000, 5000],
  defaultPageSize: 25,
  maxPageSizeForLists: 100,

  //eindeutiges Token für den Service
  serviceType: new InjectionToken<AbstractEntity>('serviceType')
}
