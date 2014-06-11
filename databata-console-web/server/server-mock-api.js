exports.addRoutes = function (app) {
  require('./api-mock/mock-databata-api').addRoutes(app);
};