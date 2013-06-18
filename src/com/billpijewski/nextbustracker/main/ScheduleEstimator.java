package com.billpijewski.nextbustracker.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;

import com.billpijewski.nextbustracker.errors.InvalidArgumentException;
import com.billpijewski.nextbustracker.model.Direction;
import com.billpijewski.nextbustracker.model.Line;
import com.billpijewski.nextbustracker.model.StationArrival;
import com.billpijewski.nextbustracker.model.Stop;
import com.billpijewski.nextbustracker.model.Trip;

/**
 * Read in a list of arrivals, and generate estimates of transit time between
 * certain stations.
 * 
 * @author Bill Pijewski
 */
public class ScheduleEstimator {
	private Logger logger;
	private Line line;
	private List<StationArrival> arrivals;

	public static void main(String[] args) throws InvalidArgumentException,
			JDOMException, IOException {
		if (args.length != 6) {
			System.err
					.println("usage: <agency> <route> <arrivals file> <route ID> <from> <to>");
			System.exit(1);
		}

		String agency = args[0];
		String route = args[1];
		String resultsFile = args[2];
		String directionId = args[3];
		String fromStop = args[4];
		String toStop = args[5];

		ScheduleEstimator se = new ScheduleEstimator(agency, route, resultsFile);

		LineReader lr = new LineReader(agency, route);
		Line line = lr.readLineConfig();

		Direction direction = line.getDirection(directionId);
		Stop from = direction.getStop(fromStop);
		Stop to = direction.getStop(toStop);

		List<Trip> trips = se.generateTrips(line, direction, from, to);
		se.printTrips(trips);
	}

	/**
	 * @param arrivals
	 * @throws IOException
	 * @throws JDOMException
	 */
	public ScheduleEstimator(String agency, String route, String resultsFile)
			throws JDOMException, IOException  {
		super();

		logger = Logger.getLogger(ArrivalPredictor.class);
		logger.setLevel(Level.DEBUG);

		ArrivalPredictor ap = new ArrivalPredictor(agency, route);

		FileReader freader = new FileReader(new File(resultsFile));
		List<StationArrival> arrivals = ap.readVehicleArrivals(freader);
	
		this.arrivals = arrivals;

		LineReader lr = new LineReader(agency, route);
		this.line = lr.readLineConfig();
	}

	/**
	 * @param line
	 * @param direction
	 * @param from
	 * @param to
	 * @return
	 */
	public List<Trip> generateTrips(Line line, Direction direction, Stop from,
			Stop to) {
		List<Trip> trips = new ArrayList<Trip>();

		int last = 0;
		int current;
		int next = 0;
		
		while (last < arrivals.size()) {
			if ((current = findNext(from, direction, "", last)) == -1) {
				break;
			}
			
			StationArrival sa = arrivals.get(current);
			next = findNext(to, direction, sa.getVehicleId(), current + 1);
			last = current + 1;
			
			if (next != -1) {
				Trip trip = new Trip(sa, arrivals.get(next));
				trips.add(trip);
			}
		}

		return (trips);
	}
	
	public void printTrips(List<Trip> trips) {
		for (Trip trip: trips) { 
			StationArrival start = trip.getStart();
			StationArrival end = trip.getEnd();

			System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%d min\t%s",
					start.getLine().getName(),
					start.getDirection().getTag(),
					start.getStop().getStopId(),
					end.getStop().getStopId(),
					ArrivalPredictor.formatTimestamp(start.getTimestamp()),
					trip.getDuration(),
					start.getVehicleId()));
		}

	}

	private int findNext(Stop stop, Direction direction, String vehicleId,
			int offset) {
		
		// XXX I'm seeing bugs in the muni data such that the same vehicle ID is
		// occurring multiple times at different portions of the track. I think
		// the best thing is to search for the exact next station (i.e. if
		// current is Powell, look for Montgomery) to avoid those cases.

		for (int ii = offset; ii < arrivals.size(); ii++) {
			StationArrival sa = arrivals.get(ii);

			if (sa.getStop().getStopId().equals(stop.getStopId())
					&& sa.getDirection().getTag().equals(direction.getTag())
					&& (vehicleId.length() == 0 || vehicleId.equals(sa
							.getVehicleId())))

				return (ii);
		}

		return (-1);
	}
}