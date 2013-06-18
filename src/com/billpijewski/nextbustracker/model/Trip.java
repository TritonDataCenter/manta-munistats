package com.billpijewski.nextbustracker.model;

/**
 * This class contains two StationArrival objects, one each for the start and
 * end of a particular vehicle's trip.
 * 
 * @author Bill Pijewski
 */
public final class Trip {
	
	private StationArrival start;
	private StationArrival end;
	
	/**
	 * @param start
	 * @param end
	 */
	public Trip(StationArrival start, StationArrival end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public StationArrival getStart() {
		return (this.start);
	}
	
	public StationArrival getEnd() {
		return (this.end);
	}
	
	public long getDuration() {
		return ((end.getTimestamp() - start.getTimestamp()) / 1000 / 60);
	}
}