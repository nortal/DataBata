module.exports = function (grunt) {
  "use strict";

  require('time-grunt')(grunt);

  grunt.loadNpmTasks('grunt-bump');
  grunt.loadNpmTasks('grunt-bg-shell');
  grunt.loadNpmTasks('grunt-contrib-jshint');

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-jade');
  grunt.loadNpmTasks('grunt-html2js');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-htmlmin');
  grunt.loadNpmTasks('grunt-contrib-imagemin');
  grunt.loadNpmTasks('grunt-hashres');
  grunt.loadNpmTasks('grunt-file-info');
  grunt.loadNpmTasks('grunt-contrib-watch');

  var pkg = grunt.file.readJSON('package.json'),
    project = require('./project.config.js'),
    personal = require('./personal.config.js');

  project.dist_src_prefix = 'dist_src_prefix';
  project.dist_src_suffix = 'dist_src_suffix';

  grunt.option('pkg_name', pkg.name);
  grunt.option('pkg_version', pkg.version);

  var config = {
    meta: {
      banner: '/** <%= grunt.option("pkg_name") %> - v<%= grunt.option("pkg_version") %> - <%= grunt.template.today("yyyy-mm-dd") %> */\n'
    },
    bump: {
      options: {
        files: [ 'package.json' ],
        commit: false,
        commitMessage: 'chore(release): v%VERSION%',
        commitFiles: [ 'package.json' ],
        createTag: false,
        tagName: 'v%VERSION%',
        tagMessage: 'Version %VERSION%',
        push: false,
        pushTo: 'origin'
      }
    },
    jshint: {
      module: {
        options: {
          jshintrc: "etc/jshint/devel"
        },
        src: ['src/<%= grunt.option("module_path") %>/**/*.js'],
        test: ['src/<%= grunt.option("module_path") %>/**/*.spec.js'],
        gruntfile: ['gruntfile.js']}
    },
    bgShell: {
      node: {
        cmd: 'npm update --save'
      },
      bower: {
        cmd: 'bower install'
      },
      server: {
        cmd: 'node server.js'
      },
      proxyserver: {
        cmd: 'node server-proxy.js'
      },
      _defaults: {
        stdout: true,
        stderr: true,
        done: function () {
        }
      }
    },
    clean: {
      build: ['build'],
      templates: ['build/templates'],
      onejs: ['build/*.js', '!build/scripts.js']
    },
    copy: {
      assets: {
        files: [
          {
            src: project.assets.concat([project.src.assets]),
            dest: 'build',
            cwd: 'src',
            expand: true
          }
        ]
      },
      html: {
        files: [
          {
            src: [ '**/*.html' ],
            dest: 'build/templates',
            cwd: 'src',
            expand: true
          }
        ]
      }
    },
    less: {
      styles: {
        options: {
          cleancss: false
        },
        files: {
          'build/styles.css': 'src/' + project.src.styles
        }
      }
    },
    jade: {
      build: {
        files: [
          {
            src: [ '**/*.jade', '!index.jade' ],
            dest: 'build/templates',
            cwd: 'src',
            expand: true,
            ext: '.html'
          }
        ]
      },
      index_dev: {
        files: getJadeConfigIndexFiles(),
        options: getJadeConfigIndexOptions(true, false, false)
      },
      index_prod_htmlfiles: {
        files: getJadeConfigIndexFiles(),
        options: getJadeConfigIndexOptions(false, false, false)
      },
      index_prod_html2js: {
        files: getJadeConfigIndexFiles(),
        options: getJadeConfigIndexOptions(false, true, false)
      },
      index_prod_onejs: {
        files: getJadeConfigIndexFiles(),
        options: getJadeConfigIndexOptions(false, true, true)
      }
    },
    concat: {
      libs: {
        src: project.libs || [],
        dest: 'build/libs.js'
      },
      srcs: {
        options: {
          banner: getDistSrcBanner(false),
          footer: getDistSrcFooter()
        },
        src: getDistSrcFiles(),
        dest: 'build/srcs.js'
      },
      srcs_with_template: {
        options: {
          banner: getDistSrcBanner(true),
          footer: getDistSrcFooter()
        },
        src: getDistSrcFiles(),
        dest: 'build/srcs.js'
      },
      onejs: {
        src: ['build/libs.js', 'build/srcs.js', 'build/templates.js'],
        dest: 'build/scripts.js'
      }
    },
    html2js: {
      templates: {
        options: {
          module: getTemplatesModuleName(),
          base: 'build'
        },
        src: [ 'build/templates/**/*.html' ],
        dest: 'build/templates.js'
      }
    },
    cssmin: {
      styles: {
        options: {
          keepSpecialComments: 0
        },
        src: 'styles.css',
        cwd: 'build',
        dest: 'build',
        expand: true
      }
    },
    htmlmin: {
      templates: {
        options: {
          removeComments: true,
          collapseWhitespace: true
        },
        files: [
          {
            src: [ '**/*.html' ],
            dest: 'build',
            cwd: 'build',
            expand: true
          }
        ]
      }
    },
    imagemin: {
      assets: {
        files: [
          {
            expand: true,
            cwd: 'build/assets',
            src: ['**/*.{png,jpg,gif}'],
            dest: 'build/assets'
          }
        ]
      }
    },
    uglify: {
      options: {
        report: 'min',
        compress: {
          drop_console: true
        }
      },
      libs: {
        files: {
          'build/libs.js': ['build/libs.js']
        }
      },
      srcs: {
        files: {
          'build/srcs.js': ['build/srcs.js']
        }
      },
      templates: {
        files: {
          'build/templates.js': ['build/templates.js']
        }
      },
      scripts: {
        files: {
          'build/scripts.js': ['build/scripts.js']
        }
      }
    },
    hashres: {
      options: {
        encoding: 'utf8',
        fileNameFormat: '${hash}.${name}.${ext}',
        renameFiles: true
      },
      dist: {
        src: [
          'build/styles.css',
          'build/libs.js',
          'build/srcs.js',
          'build/templates.js',
          'build/scripts.js'
        ],
        dest: 'build/index.html'
      }
    },
    file_info: {
      source_files: {
        src: ['build/*.js', 'build/*.css', 'build/*.html']
      }
    },
    watch: {
      options: {
        livereload: true
      },
      assets: {
        files: [ 'src/assets/**/*' ],
        tasks: [ 'copy:assets' ]
      },
      style: {
        files: [ 'src/styles/*.less', 'src/styles/*.css' ],
        tasks: [ 'less:styles' ]
      },
      html: {
        files: [ 'src/**/*.html' ],
        tasks: [ 'copy:html' ]
      },
      jade: {
        files: [ 'src/**/*.jade' ],
        tasks: [ 'jade:index_dev', 'jade:build' ]
      },
      js: {
        files: 'src/**/*.js',
        tasks: [],
        options: {
          event: [ 'changed' ]
        }
      },
      js_list: {
        files: 'src/**/*.js',
        tasks: [ 'jade:index_dev' ],
        options: {
          event: [ 'added', 'deleted' ]
        }
      }
    }
  };

  grunt.registerTask("ngmin", "ngmin before uglify.", function () {
    ngminFiles(['build/srcs.js']);
  });

  function ngminFiles(files) {
    var ngmin = require('ngmin');
    grunt.util._.each(files, function (file) {
      var content = grunt.file.read(file);
      content = ngmin.annotate(content);
      grunt.file.write(file, content);
    });
  }

  function getTemplatesModuleName() {
    return project.ng_root_app + '.templates';
  }

  function getAppDefinition(with_template) {
    var deps = "'" + project.angular_deps.join("','") + "'" || "";
    if (with_template) {
      deps += ",'" + getTemplatesModuleName() + "'";
    }
    return 'angular.module("' + project.ng_root_app + '", [' + deps + ']);\n';
  }

  function getDistSrcBanner(with_template) {
    return '(function (window, document, angular, _, undefined) {\n' +
      '"use strict";\n' +
      'var app = ' + getAppDefinition(with_template);
  }

  function getDistSrcFooter() {
    return '}(window, document, window.angular, window._));';
  }

  function getDistSrcFiles() {
    var result = [];
    result.push('build/' + project.dist_src_prefix);
    pushSrcJsFiles(result);
    result.push('build/' + project.dist_src_suffix);
    return result;
  }

  function hasExtension(filename, ext) {
    return !filename || filename.indexOf(ext, filename.length - ext.length) !== -1
  }

  function createDevBase(basejs) {
    grunt.file.write('build/' + basejs, 'window.app = ' + getAppDefinition());
  }

  function pushSrcJsFiles(array) {
    grunt.file.recurse('src', function (abspath, rootdir, subdir, filename) {
      var subdirString = subdir !== undefined ? subdir + '/' : '';
      if (hasExtension(filename, '.js')) {
        array.push('src/' + subdirString + filename);
      }
    });
  }

  function getDevIndexJsFiles() {
    var basejs = 'base.js';
    createDevBase(basejs);

    var result = project.libs || [];
    result.push(basejs);
    pushSrcJsFiles(result);
    return result;
  }

  function getProdIndexJsFiles(nohtml) {
    var result = ['libs.js', 'srcs.js'];
    if (nohtml) {
      result.push('templates.js');
    }
    return result;
  }

  function getJadeConfigIndexFiles() {
    return [
      {src: [ 'index.jade' ], dest: 'build', cwd: 'src', expand: true, ext: '.html'}
    ];
  }

  function getJadeConfigIndexOptions(dev, nohtml, onejs) {
    return {
      data: function (dest, src) {
        return {
          default_lang: personal.default_lang,
          ng_root_app: project.ng_root_app,
          ng_root_controller: project.ng_root_controller,
          style_file: 'styles.css',
          js_files: onejs ? ['scripts.js'] : (dev ? getDevIndexJsFiles() : getProdIndexJsFiles(nohtml))
        }
      }
    };
  }

  function getBuildTargets(dev, nohtml, onejs) {
    var result = ['copy:assets', 'copy:html', 'less:styles', 'jade:build'];
    if (dev) {
      return result.concat(['jade:index_dev']);
    }

    if (nohtml) {
      result.push('concat:srcs_with_template');
    } else {
      result.push('concat:srcs');
    }
    result = result.concat(['concat:libs', 'cssmin:styles', 'ngmin']); // 'imagemin:assets'

    if (nohtml) {
      result.push('html2js:templates');
      result.push('clean:templates');
    } else {
      result.push('htmlmin:templates');
    }

    if (onejs) {
      result.push('concat:onejs');
      result.push('clean:onejs');
      result.push('uglify:scripts');
    } else {
      result.push('uglify:libs');
      result.push('uglify:srcs');
      if (nohtml) {
        result.push('uglify:templates');
      }
    }

    if (onejs) {
      result.push('jade:index_prod_onejs');
    } else if (nohtml) {
      result.push('jade:index_prod_html2js');
    } else {
      result.push('jade:index_prod_htmlfiles');
    }

    result.push('hashres:dist');
    return result;
  }

  grunt.config.init(config);

  grunt.registerTask('default', ['clean:build', 'build']);
  grunt.registerTask('build', ['clean:build', 'build-fast']);
  grunt.registerTask('dist', ['clean:build', 'build-product-htmlfiles']);
  grunt.registerTask('nohtml', ['clean:build', 'build-product-html2js']);
  grunt.registerTask('onejs', ['clean:build', 'build-product-onejs']);

  grunt.registerTask('build-fast', getBuildTargets(true, false, false));
  grunt.registerTask('build-product-htmlfiles', getBuildTargets(false, false, false));
  grunt.registerTask('build-product-html2js', getBuildTargets(false, true, false));
  grunt.registerTask('build-product-onejs', getBuildTargets(false, true, true));

  grunt.registerTask('info', ['file_info']);

  grunt.registerTask('update',
    ['bgShell:node', 'bgShell:bower']
  );
  grunt.registerTask('server',
    ['bgShell:server']
  );
  grunt.registerTask('proxyserver',
    ['bgShell:proxyserver']
  );
};
