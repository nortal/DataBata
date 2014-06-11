app.config(function ($locationProvider, $modalProvider) {
  $locationProvider.html5Mode(true).hashPrefix('!');
  angular.extend($modalProvider.defaults, {template: '/templates/modal.tpl.html', html: true, show: false});
});

app.controller('RootCtrl', function ($scope, $resource, $modal, $location, $anchorScroll) {
  function reloadData() {
    data.info = api.info(function() {
      data.title = 'DataBata: ' + data.info.user + '@' + data.info.host;
    });
    var db_history = api.history(function() {
      db_history = _.sortBy(db_history, function(item) { return item.changeTime; });
      db_history.reverse();
      data.history.push.apply(data.history, db_history);
      groupHistory();
    });
    var db_objects = api.objects(function() {
      data.objects.push.apply(data.objects, db_objects);
    });
    var db_logs = api.logs(function() {
      data.logs.push.apply(data.logs, db_logs);
      var modules = _.pluck(data.logs, 'moduleName');
      data.modules.push.apply(data.modules, _.uniq(modules));
    });
  }

  function groupHistory(module) {
    var filtered = data.history;
    if (module) {
      filtered = _.where(filtered, {module_name: module});
    }
    data.history_filtered_count = filtered.length;
    data.history_filtered = _.groupBy(filtered, function(item) {
      return moment(item.changeTime).format("YYYY.MM");
    });
  }

  var api = $resource('', {}, {
    info: {
      method: 'GET',
      url: '/databata/api/info'
    },
    history: {
      method: 'GET',
      url: '/databata/api/history',
      isArray: true
    },
    logs: {
      method: 'GET',
      url: '/databata/api/logs',
      isArray: true
    },
    objects: {
      method: 'GET',
      url: '/databata/api/objects',
      isArray: true
    }
  });

  var data = {
    info: {},
    history: [],
    history_filtered: [],
    history_filtered_count: 0,
    objects: [],
    objects_order: 'objectName',
    objects_reverse: false,
    objects_filter: {},
    logs: [],
    logs_limits: [15,50,150,500],
    logs_limit: 15,
    logs_order: 'updateTime',
    logs_reverse: true,
    logs_filter: {},
    modules: ['ALL'],
    module: 'ALL'
  }
  $scope.data = data;

  $scope.formatDate = function(date) {
    return moment(date).format("YYYY.MM.DD HH:mm:ss");
  }

  $scope.formatHistoryTime = function(date) {
    return moment(date).format("DD. HH:mm:ss");
  }

  $scope.showLog = function(object_name) {
    data.logs_filter.$ = object_name;
    $location.hash('logs');
    $anchorScroll();
  }

  $scope.clickModule = function(module) {
    data.module = module;
    if (module === 'ALL') {
      data.logs_filter.moduleName = '';
      data.objects_filter.moduleName = '';
      groupHistory();
    } else {
      data.logs_filter.moduleName = module;
      data.objects_filter.moduleName = module;
      groupHistory(module);
    }
  }

  $scope.modal = function(title, content) {
    var info = $modal({
      scope: $scope,
      title: title,
      content: content
    });
    info.$promise.then(function () {
      info.show();
    });
  }

  reloadData();
});

