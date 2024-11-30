export const appDefaults = {
  serverUrl: 'http://' + window.location.hostname + ':8081/music/',
  serviceParamSuffixId: 'Id',
  serviceParamPageNumber: 'pagenumber',
  serviceParamPageSize: 'pagesize',

  pageSizes: [5, 10, 25, 50, 100, 250, 500, 1000],
  defaultPageSize: 10,

  useLocalFilteringInsteadSearch: false
}
