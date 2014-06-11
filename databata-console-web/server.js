// [RUS] http://jsman.ru/express/
// [ENG] https://github.com/visionmedia/express

require('express-namespace');
var express = require('express'),
  http = require('http'),
  path = require('path'),
  conf = require('./personal.config.js');

var app = express();
app.set('port', conf.node_port);
app.set('base', 'build');

app.use(express.logger());
app.use(express.bodyParser());
app.use(express.compress());
app.use(express.methodOverride());
app.use(express.errorHandler());

app.use(express.static(__dirname));
app.use(express.static(path.join(__dirname, app.get('base'))));
app.use(app.router);

require('./server/server-mock-api').addRoutes(app);

app.get('*', function (req, res) {
  res.sendfile('index.html', { root: path.join(__dirname, app.get('base')) });
});

http.createServer(app).listen(app.get('port'));

console.log('Express server on "' + app.get('base') + '" listening on port ' + app.get('port'));