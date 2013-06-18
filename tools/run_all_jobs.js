#!/usr/bin/env node

var cp = require('child_process');
var fs = require('fs');
var sprintf = require('util').format;

var lines = JSON.parse(fs.readFileSync('./14_stops.json', 'utf8'));

lines.forEach(function (line) {
	for (var ii = 0; ii < line.stops.length; ii++) {
		for (var jj = ii + 1; jj < line.stops.length; jj++) {
			var cmd = '../jobs/bin/run_trips.js';
			var argv = [ '14', line.direction, line.stops[ii], line.stops[jj] ];

			cp.execFile(cmd, argv, function (err, stdout, stderr) {
				if (err)
					throw (err);

				console.log(stdout);

				var job = JSON.parse(stdout);
				console.log(job);
			});
		}
	}
});
