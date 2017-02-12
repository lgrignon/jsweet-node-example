var gulp     = require('gulp'),
    uglify       = require('gulp-uglify'),
    rename       = require('gulp-rename'),
    minifyCSS    = require('gulp-minify-css'),
    concat       = require('gulp-concat'),
    notify       = require("gulp-notify"),
    jade         = require("gulp-jade"),
    plumber      = require("gulp-plumber"),
    shell        = require('gulp-shell'),
    runSequence  = require('run-sequence');

function onError(error) {
	console.log('FATAL ERROR: ' + error);
	console.log(error);
	
	console.log('----- EXITING -----');
	
	process.exit(1);
}

gulp.task('clean', shell.task([
	'mvn clean -P server',
	'mvn clean -P client'
]));


gulp.task('buildServer', shell.task(
	'mvn generate-sources -P server'
));

gulp.task('buildClient', shell.task(
	'mvn generate-sources -P client'
));

gulp.task('buildClientAndServer', shell.task([
	'mvn generate-sources -P server',
	'mvn generate-sources -P client'
]));

//VENDORS
gulp.task('bundleJsVendors', function() {
    return gulp.src([
        'bower_components/jquery/dist/jquery.min.js',
        'bower_components/hammerjs/hammer.min.js',
        'bower_components/angular/angular.js',
        'node_modules/socket.io-client/dist/socket.io.js',
        'bower_components/angular-route/angular-route.js',
        'bower_components/angular-aria/angular-aria.js',
        'bower_components/angular-animate/angular-animate.js',
        'bower_components/angular-material/angular-material.js',
        'bower_components/ng-socket-io/ng-socket-io.js',
    ])

//        .pipe(plumber())
        .pipe(concat('vendors.js'))
        .pipe(uglify())
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest('build/js/'))
        .on('error', onError);
});

gulp.task('bundleCss', function () {
    return gulp.src([
        'bower_components/foundation/css/foundation.css',
        'bower_components/angular-material/angular-material.css',
		'css/style.css',
    ])
    .pipe(plumber())
    .pipe(minifyCSS())
    .pipe(concat('bundle.css'))
    .pipe(gulp.dest('build/css'))
    .on('error', onError);
});

gulp.task('jade', function() {
	return gulp.src([
	     'index.jade',
     ])
     .pipe(jade())
     .pipe(gulp.dest('build/'))
     .on('error', onError);
});

gulp.task('views', function() {
	return gulp.src([
	    'views/**/*',
     ])
     .pipe(gulp.dest('build/views'))
     .on('error', onError);
});

gulp.task('images', function() {
	return gulp.src([
	    'img/**/*',
     ])
     .pipe(gulp.dest('build/img'))
     .on('error', onError);
});


gulp.task('static', ['jade', 'bundleJsVendors', 'bundleCss', 'views', 'images']);

gulp.task('all', ['clean'], function (cb) {
    runSequence(['buildClientAndServer', 'static'], cb);
});