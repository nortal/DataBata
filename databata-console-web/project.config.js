module.exports = {
  src: {
    styles: 'styles/custom.less',
    assets: 'assets/**/*'
  },
  ng_root_app: 'databata',
  ng_root_controller: 'RootCtrl',
  angular_deps: ['ngSanitize', 'ngResource', 'mgcrea.ngStrap'],
  assets: [],
  libs: [
    'vendor_lib/moment/moment.js',
    'vendor_lib/underscore/underscore.js',
    'vendor_lib/jquery/dist/jquery.js',
    'vendor_lib/bootstrap/dist/js/bootstrap.js',
    'vendor_lib/angular/angular.js',
    'vendor_lib/angular-sanitize/angular-sanitize.js',
    'vendor_lib/angular-resource/angular-resource.js',
    'vendor_lib/angular-strap/dist/angular-strap.js',
    'vendor_lib/angular-strap/dist/angular-strap.tpl.js'
  ]
};