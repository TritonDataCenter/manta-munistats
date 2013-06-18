package com.billpijewski.nextbustracker.model;

/**
 * A particular bus stop. Contains the stop's identifier, name, latitude, and
 * longitude.
 * 
 * @author Bill Pijewski
 */
public class Stop {
	private String tag;
	private String title;
	private Float lat;
	private Float lon;
	private String stopId;
	
	/**
	 * @param tag
	 * @param title
	 * @param lat
	 * @param lon
	 * @param stopId
	 */
	public Stop(String tag, String title, Float lat, Float lon, String stopId) {
		super();
		this.tag = tag;
		this.title = title;
		this.lat = lat;
		this.lon = lon;
		this.stopId = stopId;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the lat
	 */
	public Float getLat() {
		return lat;
	}

	/**
	 * @return the lon
	 */
	public Float getLon() {
		return lon;
	}

	/**
	 * @return the stopId
	 */
	public String getStopId() {
		return stopId;
	}
	
	public String toString() {
		return ("[" + tag + ", " + title + "]");
	}
}