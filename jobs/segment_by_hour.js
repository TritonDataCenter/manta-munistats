#!/usr/bin/env node

/*
 * segment_by_hour.js: read in a bunch of records like:
 * 
 *     M       M__OB1  16994   16259   02/18/2013   06:27:06   30 min  1445
 *     M       M__OB1  16994   16259   02/18/2013   06:45:26   25 min  1420
 *     M       M__OB1  16994   16259   02/18/2013   07:04:07   29 min  1472
 *
 * and produce a report breaking down all those trips by hour and day.
 */

var fs = require('fs');
var sprintf = require('sprintf').sprintf;
var statsjs = require('statsjs');

if (process.argv.length !== 3) {
	console.error('usage: ' + process.argv[0] + ' <file>');
	process.exit(1);
}

var file = process.argv[2];

var contents = fs.readFileSync(file, 'utf8');
var lines = contents.split('\n');

var results = {};

for (var ii = 0; ii < 24; ii++) {
	results[ii] = {};
	results[ii][0] = [];
	results[ii][1] = [];
}

lines.forEach(function (line) {
	var tokens = line.split('\t');

	if (tokens.length < 6)
		return;

	var datestamp = new Date(tokens[4]);
	var duration = parseInt(tokens[5].split(' ')[0], 10);

	var hour = datestamp.getHours();
	var day = datestamp.getDay();

	var is_weekend = (day === 0 || day === 6) ? 1 : 0;

	results[hour][is_weekend].push(duration);
});


for (var ii = 0; ii < 24; ii++) {
	var times = statsjs(results[ii][0]).removeOutliers().arr;

	var size = statsjs(times).size();
	var mean = statsjs(times).mean();
	var stddev = statsjs(times).stdDev();

	console.log(sprintf('Weekday %d00-%d00 %d %.2f %.2f', ii, ii + 1, size,
	    mean, stddev));
}

for (var ii = 0; ii < 24; ii++) {
	var times = statsjs(results[ii][1]).removeOutliers().arr;

	var size = statsjs(times).size();
	var mean = statsjs(times).mean();
	var stddev = statsjs(times).stdDev();

	console.log(sprintf('Weekend %d00-%d00 %d %.2f %.2f', ii, ii + 1, size,
	    mean, stddev));
}
