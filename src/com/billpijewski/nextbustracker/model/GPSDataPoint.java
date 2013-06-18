package com.billpijewski.nextbustracker.model;

/**
 * @author Bill Pijewski
 */
public class GPSDataPoint {
	private String id;
	private Line line;
	private Direction direction;
	private Float currentLat;
	private Float currentLon;
	
	/**
	 * @param id
	 * @param line
	 * @param direction
	 * @param currentLat
	 * @param currentLon
	 */
	public GPSDataPoint(String id, Line line, Direction direction, Float currentLat,
			Float currentLon) {
		super();
		this.id = id;
		this.line = line;
		this.direction = direction;
		this.currentLat = currentLat;
		this.currentLon = currentLon;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the line
	 */
	public Line getLine() {
		return line;
	}
	/**
	 * @return the route
	 */
	public Direction getDirection() {
		return direction;
	}
	/**
	 * @return the currentLat
	 */
	public Float getCurrentLat() {
		return currentLat;
	}
	/**
	 * @return the currentLon
	 */
	public Float getCurrentLon() {
		return currentLon;
	}
	
	public String toString() {
		return ("[" + id + ", " + line.getName() + "]");
	}
}
