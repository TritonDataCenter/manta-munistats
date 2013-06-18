package com.billpijewski.nextbustracker.model;

import java.util.LinkedList;
import java.util.List;

import com.billpijewski.nextbustracker.errors.InvalidArgumentException;

public class Direction {
	private List<Stop> stops;
	private String title;
	private String name;
	private String tag;
	
	/**
	 * @param stops
	 * @param title
	 * @param name
	 */
	public Direction(List<Stop> stops, String title, String name, String tag) {
		super();
		this.stops = stops;
		this.title = title;
		this.name = name;
		this.tag = tag;
	}

	public Stop getStop(String stopId) throws InvalidArgumentException {
		for (Stop stop : stops) {
			if (stop.getStopId().equals(stopId))
				return (stop);
		}

		throw new InvalidArgumentException("invalid stop for " + tag + ": "
				+ stopId);
	}
	
	/**
	 * @return the stops
	 */
	public List<Stop> getStops() {
		return stops;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * Return a list of stops (if any) between two stations.
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception 
	 */
	public List<Stop> stopsBetween(Stop from, Stop to) throws Exception {
		List<Stop> between = new LinkedList<Stop>();

		int from_idx = stops.indexOf(from);
		if (from_idx == -1)
			throw new Exception("Stop " + from.getTag() + " not on line " + tag);

		int to_idx = stops.indexOf(to);
		if (to_idx == -1)
			throw new Exception("Stop " + to.getTag() + " not on line " + tag);

		for (int ii = from_idx + 1; ii < to_idx; ii++)
			between.add(stops.get(ii));

		return (between);
	}
	
	public boolean areAdjacent(Stop from, Stop to) throws Exception {
		int from_idx = stops.indexOf(from);
		if (from_idx == -1)
			throw new Exception("Stop " + from.getTag() + " not on line " + tag);

		int to_idx = stops.indexOf(to);
		if (to_idx == -1)
			throw new Exception("Stop " + to.getTag() + " not on line " + tag);
		
		return (to_idx == (from_idx + 1));
	}
}