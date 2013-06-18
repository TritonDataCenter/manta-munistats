package com.billpijewski.nextbustracker.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.billpijewski.nextbustracker.errors.InvalidArgumentException;
import com.billpijewski.nextbustracker.model.Line;
import com.billpijewski.nextbustracker.model.Direction;
import com.billpijewski.nextbustracker.model.Stop;
import com.billpijewski.nextbustracker.model.GPSDataPoint;
import com.billpijewski.nextbustracker.model.StationArrival;

/**
 * This class reads all the vehicle observations for a given line and generates
 * a transcript of when each vehicle arrived at a given stop.
 * 
 * @author Bill Pijewski
 */
public class ArrivalPredictor {

	private Logger logger;
	private Line line;
	static Double MAX_STOP_GAP = 0.5;

	public static void main(String[] args) throws Exception {
		if (args.length != 3 && args.length != 4) {
			System.err
					.println("usage: ArrivalPredictor <agency> <route> <input dir> [ <results file>] ");
			System.exit(1);
		}

		String agency = args[0];
		String route = args[1];
		String dataFiles = args[2];

		ArrivalPredictor sp = new ArrivalPredictor(agency, route);

		List<StationArrival> stops = sp.generateArrivalTimeline(dataFiles);

		Writer out;
		if (args.length == 4) {
			String resultsFile = args[3];
			File results = new File(resultsFile);
			out = new FileWriter(results);
		} else {
			out = new OutputStreamWriter(System.out);
		}

		sp.writeVehicleArrivals(stops, out);
		out.flush();
	}

	public ArrivalPredictor(String agency, String routeId)
			throws JDOMException, IOException {
		super();

		logger = Logger.getLogger(ArrivalPredictor.class);
		logger.setLevel(Level.DEBUG);

		LineReader lr = new LineReader(agency, routeId);
		this.line = lr.readLineConfig();
	}

	public static String formatTimestamp(Long timestamp) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		cal.setTimeInMillis(timestamp);

		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		return (String.format("%02d/%02d/%4d   %02d:%02d:%02d", month, date,
				year, hour, minute, second));
	}

	private List<GPSDataPoint> loadGPSDataPoints(Line line, Reader reader)
			throws IOException, InvalidArgumentException {
		List<GPSDataPoint> datapoints = new LinkedList<GPSDataPoint>();

		SAXBuilder builder = new SAXBuilder();
		Document document = null;

		try {
			document = builder.build(reader);
		} catch (JDOMException e) {
			System.err.println("Error parsing XML file: " + e.getMessage());
			e.printStackTrace();
			return (datapoints);
		}

		Element root = document.getRootElement();

		for (Element elem : root.getChildren("vehicle")) {
			String id = elem.getAttributeValue("id");
			String dirTag = elem.getAttributeValue("dirTag");

			Float lat = Float.parseFloat(elem.getAttributeValue("lat"));
			Float lon = Float.parseFloat(elem.getAttributeValue("lon"));

			Boolean predictable = Boolean.parseBoolean(elem
					.getAttributeValue("predictable"));

			if (!predictable)
				continue;

			Direction direction = line.getDirection(dirTag);

			if (direction == null)
				continue;

			datapoints.add(new GPSDataPoint(id, line, direction, lat, lon));
		}

		return (datapoints);
	}

	private Stop findNextStop(Line line, GPSDataPoint datapoint) {
		Stop prev = null;

		/*
		 * Walk along the position of each stop. The distance from the vehicle's
		 * current location to the position of the stop will decrease as the
		 * stops are closer to the vehicle, and will increase as the stops are
		 * farther away from the vehicle. As soon as this algorithm sees the
		 * distance from the vehicle to a stop increase, then that's the next
		 * stop for this vehicle.
		 * 
		 * XXX This doesn't work for lines with U-turns (thinking 33, 43, 37,
		 * and such). Perhaps I need to follow the path of each direction
		 * instead of just stepping from station to station. For now, I'm using
		 * the MAX_STOP_GAP variable to exclude stops that are more than a mile
		 * away.
		 */
		for (Stop stop : datapoint.getDirection().getStops()) {
			Double distPrev, distNext;

			if (prev != null) {
				distPrev = calculateDistance(datapoint.getCurrentLat(),
						datapoint.getCurrentLon(), prev.getLat(), prev.getLon());
			} else {
				distPrev = new Double(100000000); // "INT64_MAX"
			}

			distNext = calculateDistance(datapoint.getCurrentLat(),
					datapoint.getCurrentLon(), stop.getLat(), stop.getLon());

			if (distPrev < distNext && (distPrev < MAX_STOP_GAP)) {
				return (stop);
			}

			prev = stop;
		}

		/*
		 * In this case, the vehicle must be closest to the stop at the end of
		 * the line.
		 */
		return (prev);
	}

	/**
	 * Calculate the distance between two points.
	 * 
	 * @param fromLat
	 *            latitude of the first point
	 * @param fromLon
	 *            longitude of the first point
	 * @param toLat
	 *            latitude of the first point
	 * @param toLon
	 *            longitude of the first point
	 * @return the distance between two points
	 */
	private Double calculateDistance(Float fromLat, Float fromLon, Float toLat,
			Float toLon) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(toLat - fromLat);
		double dLng = Math.toRadians(toLon - fromLon);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(fromLat))
				* Math.cos(Math.toRadians(toLat)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double dist = earthRadius * c;

		return (dist);
	}

	/**
	 * Returns the n-th most recent arrival for this vehicle.
	 * 
	 * @param arrivals
	 * @param datapoint
	 * @param back
	 * @return
	 */
	private StationArrival findRecentArrival(List<StationArrival> arrivals,
			GPSDataPoint datapoint, int back) {
		int skipped = 0;

		for (int jj = arrivals.size() - 1; jj >= 0; jj--) {
			StationArrival arrival = arrivals.get(jj);

			if (arrival.getVehicleId().equals(datapoint.getId())) {
				skipped += 1;

				if ((skipped - 1) == back) {
					return (arrival);
				}
			}
		}

		return (null);
	}

	/**
	 * If the vehicle hasn't published its location in a while, it may have
	 * passed stops. In that case, add reports for the intermediate stops.
	 * 
	 * @param arrivals
	 * @param vehicle
	 * @param current
	 */
	private void addSkippedStops(List<StationArrival> arrivals,
			GPSDataPoint vehicle, StationArrival current) {

		StationArrival last = findRecentArrival(arrivals, vehicle, 0);

		/*
		 * If the vehicle hasn't published its location in a while, it may have
		 * passed two stops. In that case, add reports for the intermediate
		 * stops.
		 */
		if (last != null) {
			try {
				List<Stop> skipped = vehicle.getDirection().stopsBetween(
						last.getStop(), current.getStop());

				for (int ii = 0; ii < skipped.size(); ii++) {
					Double delta = (current.getTimestamp() - last
							.getTimestamp())
							* new Double(ii + 1.0 / (skipped.size() + 1.0));
					Long timestamp = last.getTimestamp() + delta.longValue();

					StationArrival arrival = new StationArrival(last.getLine(),
							last.getDirection(), skipped.get(ii), timestamp,
							last.getVehicleId(), true);
					arrivals.add(arrival);

					logger.info("Added report for skipped station: " + arrival);
				}
			} catch (Exception e) {
				logger.warn("Error: " + e.getMessage());
			}
		}
	}

	public List<StationArrival> generateArrivalTimeline(String dirname)
			throws JDOMException, IOException, InvalidArgumentException {
		/**
		 * A map of vehicle ID -> its current location
		 */
		Map<String, Stop> currentLocations = new HashMap<String, Stop>();
		ArrayList<StationArrival> arrivals = new ArrayList<StationArrival>();

		File dir = new File(dirname);
		File[] files = dir.listFiles();

		// XXX I guess this works?
		Arrays.sort(files);

		logger.info("Processing " + files.length + " files...");

		for (int ii = 0; ii < files.length; ii++) {
			String name = files[ii].getName();


			int idx = name.indexOf("_");
			int last_idx = name.lastIndexOf(".");
			String timestamp = name.substring(idx + 1, last_idx);

			long timeInMillis = Long.parseLong(timestamp);

			List<GPSDataPoint> datapoints = loadGPSDataPoints(line,
					new FileReader(files[ii]));

			logger.debug("Processing " + name);

			for (GPSDataPoint datapoint : datapoints) {
				Stop currentStop = currentLocations.get(datapoint.getId());
				Stop nextStop = findNextStop(line, datapoint);

				/*
				 * If the vehicle has advanced beyond the next station, then it
				 * must have visited that station.
				 */
				if (currentStop != null
						&& currentStop.getTag() != nextStop.getTag()) {

					StationArrival current = new StationArrival(line,
							datapoint.getDirection(), currentStop,
							timeInMillis, datapoint.getId(), false);
					StationArrival mostRecent = findRecentArrival(arrivals,
							datapoint, 0);
					StationArrival secondMostRecent = findRecentArrival(
							arrivals, datapoint, 1);

					/*
					 * Sometimes a vehicle will appear to bounce between the
					 * last two stations at the end of the line while it's
					 * stopped there. If that happens, remove those entries.
					 */
					if (secondMostRecent != null
							&& current.getLine() == secondMostRecent.getLine()
							&& current.getStop() == secondMostRecent.getStop()) {
						arrivals.remove(mostRecent);
						arrivals.remove(secondMostRecent);

						logger.info("Vehicle at end of line, removing reports "
								+ mostRecent + " and " + secondMostRecent);
					}

					addSkippedStops(arrivals, datapoint, current);

					arrivals.add(current);

					logger.debug("Added report " + current);
				}

				currentLocations.put(datapoint.getId(), nextStop);
			}
		}

		return (arrivals);
	}

	/**
	 * 
	 * @param line
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws InvalidStopException 
	 */
	public List<StationArrival> readVehicleArrivals(Reader input)
			throws IOException {
		List<StationArrival> arrivals = new ArrayList<StationArrival>();
		BufferedReader breader = new BufferedReader(input);
		String sline;

		while ((sline = breader.readLine()) != null) {
			String[] tokens = sline.split("\t");

			Direction direction = null;
			
			try {
				direction = line.getDirection(tokens[3]);
			} catch (InvalidArgumentException e) {
				continue;
			}
			
			Stop stop = null;
			
			try {
				stop = direction.getStop(tokens[4]);
			} catch (InvalidArgumentException e) {
				continue;
			}
			
			Long timestamp = Long.parseLong(tokens[1]);
			String vehicleId = tokens[2];

			StationArrival report = new StationArrival(line, direction, stop,
					timestamp, vehicleId, false);
			arrivals.add(report);
		}

		return (arrivals);
	}

	/**
	 * Write the vehicle reports out to the specified writer.
	 * 
	 * @param arrivals
	 * @param out
	 * @throws IOException
	 */
	public void writeVehicleArrivals(List<StationArrival> arrivals, Writer out)
			throws IOException {

		for (StationArrival arrival : arrivals) {
			out.write(ArrivalPredictor.formatTimestamp(arrival.getTimestamp())
					+ "\t" + arrival.getTimestamp() + "\t"
					+ arrival.getVehicleId() + "\t"
					+ arrival.getDirection().getTag() + "\t"
					+ arrival.getStop().getStopId() + "\t"
					+ arrival.getStop().getTitle() + "\n");
		}
	}
}