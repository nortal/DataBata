// [RUS] http://jsman.ru/express/
// [ENG] https://github.com/visionmedia/express

require('express-namespace');
var express = require('express'),
  http = require('http'),
  path = require('path'),
  conf = require('./config.personal.json');

var app = express();
app.set('port', conf.server.port);
app.set('base', conf.server.base);

app.use(express.logger());
app.use(express.bodyParser());
app.use(express.compress());
app.use(express.methodOverride());
app.use(express.errorHandler());

app.use(express.static(path.join(__dirname, app.get('base'))));
app.use(app.router);

//require('./server-mock-api').addRoutes(app);
app.all('/cas/*', function(req, res) {
  var request = require('request');
  console.log(req.method);
  console.log(req.url);
  request.get('https://mestest.nortal.com/' + req.url).pipe(res);

});

app.all('/api/*', function(req, res) {
  var request = require('request');
  console.log(req.method);
  console.log(req.url);
  request.get('http://mobile.mesdemo.nortal.com' + req.url).pipe(res);

});

app.get('*', function (req, res) {
  res.sendfile('index.html', { root: path.join(__dirname, app.get('base')) });
});

http.createServer(app).listen(app.get('port'));

console.log('Express server on "' + app.get('base') + '" listening on port ' + app.get('port'));