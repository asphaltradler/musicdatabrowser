export const appDefaults = {
  serverUrl: 'http://' + window.location.hostname + ':8081/music/',
  serviceParamSuffixId: 'Id',
  serviceParamName: 'name',
  serviceParamPageNumber: 'pageNumber',
  serviceParamPageSize: 'pageSize',

  pageSizes: [5, 10, 25, 50, 100, 250, 500, 1000],
  defaultPageSize: 10,
  maxPageSizeForLists: 100,

  useLocalFilteringInsteadSearch: false
}
