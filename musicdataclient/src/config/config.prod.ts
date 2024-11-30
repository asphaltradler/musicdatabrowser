export const appDefaults = {
  serverUrl: 'http://' + window.location.hostname + ':8080/music/',
  serviceParamSuffixId: 'Id',
  serviceParamPageNumber: 'pagenumber',
  serviceParamPageSize: 'pagesize',

  pageSizes: [10, 25, 50, 100, 250, 500, 1000, 5000],
  defaultPageSize: 25,

  useLocalFilteringInsteadSearch: false
}
