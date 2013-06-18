# Muni Statistics

This repository contains tools for downloading, manipulating, and analyzing the
arrival times of San Francisco Muni vehicles.  The collection agent download
each vehicle's position every 20 seconds.  Those positions are then mapped to
specific stops, and then those transcripts of vehicle arrivals are used to
aggregate statistics about the average trip length between different stops.

# Data Transformations

There are three distinct transformations used to go from a raw XML dump of a
vehicle's GPS coorindates to an aggregated report of a given line's statistics.
The NextBus API provides an XML blob with a lit of vehicles for a given line:

    <?xml version="1.0" encoding="UTF-8"?>
    <body copyright="All data copyright San Francisco Muni 2013.">
      <vehicle id="5461" routeTag="14" dirTag="14_IB1" lat="37.78429" lon="-122.40442" secsSinceReport="2" predictable="true" heading="45" speedKmHr="0" />
      <vehicle id="7112" routeTag="14" dirTag="14_IB1" lat="37.75132" lon="-122.41838" secsSinceReport="38" predictable="true" heading="356" speedKmHr="27" />
      <vehicle id="7104" routeTag="14" dirTag="14_OB2" lat="37.72193" lon="-122.437" secsSinceReport="20" predictable="true" heading="211" speedKmHr="35" />
      <vehicle id="7126" routeTag="14" dirTag="14_IB1" lat="37.71793" lon="-122.43979" secsSinceReport="20" predictable="true" heading="31" speedKmHr="36" />
      <vehicle id="7032" routeTag="14" dirTag="14_IB1" lat="37.74655" lon="-122.41923" secsSinceReport="87" predictable="true" heading="25" speedKmHr="20" />
      <vehicle id="7001" routeTag="14" dirTag="14_OB2" lat="37.7528099" lon="-122.41854" secsSinceReport="2" predictable="true" heading="175" speedKmHr="5" />
      <vehicle id="7117" routeTag="14" dirTag="14_IB1" lat="37.73587" lon="-122.42445" secsSinceReport="7" predictable="true" heading="23" speedKmHr="0" />
      <vehicle id="5450" routeTag="14" dirTag="14_OB2" lat="37.76923" lon="-122.42005" secsSinceReport="83" predictable="true" heading="175" speedKmHr="33" />
      <vehicle id="7031" routeTag="14" dirTag="14_OB2" lat="37.7069" lon="-122.46051" secsSinceReport="51" predictable="true" heading="213" speedKmHr="25" />
      <vehicle id="7049" routeTag="14" dirTag="14_IB1" lat="37.78636" lon="-122.40191" secsSinceReport="99" predictable="true" heading="31" speedKmHr="11" />
      <vehicle id="7118" routeTag="14" dirTag="14_IB1" lat="37.77416" lon="-122.4173" secsSinceReport="73" predictable="true" heading="39" speedKmHr="16" />
      <vehicle id="7124" routeTag="14" dirTag="14_OB4" lat="37.77606" lon="-122.41486" secsSinceReport="72" predictable="true" heading="225" speedKmHr="0" />
      <vehicle id="5412" routeTag="14" dirTag="14_IB1" lat="37.7069199" lon="-122.45808" secsSinceReport="32" predictable="true" heading="66" speedKmHr="29" />
      <vehicle id="7012" routeTag="14" lat="37.76363" lon="-122.40926" secsSinceReport="65" predictable="false" heading="355" speedKmHr="7" />
      <vehicle id="7043" routeTag="14" dirTag="14_IB1" lat="37.78795" lon="-122.39988" secsSinceReport="23" predictable="true" heading="45" speedKmHr="0" />
      <vehicle id="7107" routeTag="14" dirTag="14_OB2" lat="37.7421099" lon="-122.42227" secsSinceReport="44" predictable="true" heading="206" speedKmHr="23" />
      <vehicle id="7122" routeTag="14" dirTag="14_OB2" lat="37.70986" lon="-122.44962" secsSinceReport="26" predictable="true" heading="244" speedKmHr="27" />
      <vehicle id="7115" routeTag="14" dirTag="14_IB1" lat="37.78035" lon="-122.4094" secsSinceReport="17" predictable="true" heading="45" speedKmHr="22" />
      <lastTime time="1370986564897" />
    </body>

There are approximately 4000 of these files generated per line per day.  Since
they're such small files, I created a tarball for each day so as not to have too
many keys.  

The first phase of this transformation takes the tarball and generates a
transcript of which train arrives at which station:


    01/04/2013   07:29:45	1357313385102	7017	14_OB2	15604	Mission St & Mt Vernon Ave
    01/04/2013   07:30:12	1357313412137	7011	14_IB1	15586	Mission St & Excelsior Ave
    01/04/2013   07:30:12	1357313412137	7113	14_IB1	15567	Mission St & 26th St
    01/04/2013   07:30:12	1357313412137	7106	14_IB1	17129	Mission St & 7th St
    01/04/2013   07:30:12	1357313412137	7120	14_OB2	15578	Mission St & Appleton Ave
    01/04/2013   07:30:12	1357313412137	7030	14_IB1	15574	Mission St & Acton St
    01/04/2013   07:30:12	1357313412137	7104	14_OB2	15550	Mission St & 15th St
    01/04/2013   07:30:32	1357313432503	7045	14_OB2	15528	Mission St & 1st St
    01/04/2013   07:30:32	1357313432503	7017	14_OB2	15589	Mission St & Foote Ave
    01/04/2013   07:30:52	1357313452529	7017	14_OB2	15602	Mission St & Lowell St
    01/04/2013   07:31:12	1357313472236	7106	14_IB1	15537	Mission St & 6th St
    01/04/2013   07:31:12	1357313472236	7045	14_OB2	15529	Mission St & 2nd St
    01/04/2013   07:31:12	1357313472236	7104	14_OB2	15552	Mission St & 16th St
    01/04/2013   07:31:33	1357313493796	7011	14_IB1	15620	Mission St & Silver Ave
    01/04/2013   07:31:33	1357313493796	7113	14_IB1	15565	Mission St & 24th St
    01/04/2013   07:31:33	1357313493796	7120	14_OB2	15597	Mission St & Highland Ave
    01/04/2013   07:31:33	1357313493796	7030	14_IB1	15608	Mission St & Oliver St
    01/04/2013   07:31:33	1357313493796	7017	14_OB2	15627	Mission St & Whittier St
    01/04/2013   07:31:52	1357313512961	7011	14_IB1	15624	Mission St & Trumbull St
    01/04/2013   07:32:14	1357313534015	7113	14_IB1	15563	Mission St & 23rd St

The job which generates that report is driven by jobs/arrivals.json and
specifically the jobs/generate_arrivals.sh script.  The output of that job is a
single key (per line, per day) which is the transcript of train arrivals.

Next, all the transcripts for a given line are fed into the next phase, which
calculates the total duration for each trip between two stations.  The output
format is:

    M	M__OB1	16994	16259	06/11/2013   13:59:47	31 min	1419
    M	M__OB1	16994	16259	06/11/2013   14:18:23	29 min	1420
    M	M__OB1	16994	16259	06/11/2013   14:21:06	28 min	1440
    M	M__OB1	16994	16259	06/11/2013   14:21:06	28 min	1458
    M	M__OB1	16994	16259	06/11/2013   14:33:24	37 min	1438
    M	M__OB1	16994	16259	06/11/2013   14:43:26	31 min	1468
    M	M__OB1	16994	16259	06/11/2013   14:53:28	21 min	1516
    M	M__OB1	16994	16259	06/11/2013   15:06:48	36 min	1479
    M	M__OB1	16994	16259	06/11/2013   15:26:44	29 min	1410

That shows a random sample of trips from the Joyent office back to my apartment.

Finally, the last phase takes the list of trips between two stops and generates
a report of average duration and standard deviation by hour:

    Weekday 000-100 25 28.80 3.06
    Weekday 100-200 0 NaN 0.00
    Weekday 200-300 0 NaN 0.00
    Weekday 300-400 0 NaN 0.00
    Weekday 400-500 11 29.09 0.30
    Weekday 500-600 26 29.69 1.05
    Weekday 600-700 62 28.98 4.55
    Weekday 700-800 80 30.11 5.98
    Weekday 800-900 91 29.99 5.64
    Weekday 900-1000 103 31.87 4.98
    Weekday 1000-1100 104 30.20 5.79
    Weekday 1100-1200 92 30.17 5.17
    Weekday 1200-1300 89 30.69 3.67
    Weekday 1300-1400 86 30.71 3.49
    Weekday 1400-1500 83 29.20 4.92
    Weekday 1500-1600 99 30.20 5.38
    Weekday 1600-1700 107 29.93 5.73
    Weekday 1700-1800 102 32.63 6.36
    Weekday 1800-1900 87 30.10 5.74
    Weekday 1900-2000 91 29.85 3.87
    Weekday 2000-2100 58 29.76 2.99
    Weekday 2100-2200 30 29.80 1.88
    Weekday 2200-2300 34 29.06 2.68
    Weekday 2300-2400 19 29.84 0.96
    Weekend 000-100 7 29.43 3.31
    Weekend 100-200 0 NaN 0.00
    Weekend 200-300 0 NaN 0.00
    Weekend 300-400 0 NaN 0.00
    Weekend 400-500 0 NaN 0.00
    Weekend 500-600 0 NaN 0.00
    Weekend 600-700 3 29.00 0.00
    Weekend 700-800 6 27.50 3.89
    Weekend 800-900 12 30.75 3.02
    Weekend 900-1000 14 31.14 4.20
    Weekend 1000-1100 11 27.09 5.63
    Weekend 1100-1200 17 33.12 4.36
    Weekend 1200-1300 16 31.94 4.14
    Weekend 1300-1400 17 29.24 4.37
    Weekend 1400-1500 17 30.47 4.87
    Weekend 1500-1600 16 30.81 5.19
    Weekend 1600-1700 20 31.65 3.84
    Weekend 1700-1800 20 29.20 4.69
    Weekend 1800-1900 19 30.89 4.07
    Weekend 1900-2000 10 31.40 2.84
    Weekend 2000-2100 10 33.30 5.36
    Weekend 2100-2200 14 28.21 4.68
    Weekend 2200-2300 7 29.86 0.38
    Weekend 2300-2400 5 31.60 3.21

The data show roughly what I would expect: there are many more trains during the
rush hours, and both the average trip length and standard deviation are higher
during that time as well.

# Lessons Learned

- It served me well to build the tarball of small files as opposed to upload
  each XML blob as a single key.  It was easy enough to untar the tarball inside
  the job, made running jobs on those keys faster, and was easier to debug as I
  didn't have 100000s of keys in a single directory.
- Manipulating XML sucks.  I thought it was a good idea to keep the original
  data intact as received from the API, but it would have been better to go
  straight to a text, column-based format.
- For the second transforation, I ran a job for each (source, destination) pair.
  For example, different jobs for (Powell, Civic Center), (Powell, Church St.),
  (Powell, Castro), (Civic Center, Church St.) and so on.  It would have been
  easier/faster to do that in the job itself, and just have my program emit a
  key for each (source, destination) pair.  For some of the longer lines, there
  are 2000-2500 pairs, so running that many jobs became untenable.

# TODO

- Scripts to automate/simplify running of jobs
- Small frontend to interact with API
- Move data to /public?
