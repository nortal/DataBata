exports.addRoutes = function (app) {

  app.namespace('/databata/api', function () {

    app.get('/info', function (req, res) {
      res.send(require('./data_info').get());
    });
    app.get('/history', function (req, res) {
      res.send(require('./data_history').getList());
    });
    app.get('/logs', function (req, res) {
      res.send(require('./data_sql_logs').getList());
    });
    app.get('/objects', function (req, res) {
      res.send(require('./data_objects').getList());
    });

    app.get('*', function (req, res) {
      res.status('404').send('Default fail on get...');
    });
    app.post('*', function (req, res) {
      res.status('404').send('Default fail on post...');
    });
    app.put('*', function (req, res) {
      res.status('404').send('Default fail on put...');
    });
    app.delete('*', function (req, res) {
      res.status('404').send('Default fail on delete...');
    });
  });

};