package com.billpijewski.nextbustracker.model;

import com.billpijewski.nextbustracker.main.ArrivalPredictor;

public class StationArrival implements Comparable<StationArrival> {
	private Line line;
	private Direction direction;
	private Stop stop;
	private Long timestamp;
	private String vehicleId;
	private boolean generated;
	
	/**
	 * @param line
	 * @param direction
	 * @param stop
	 * @param timestamp
	 * @param vehicleId
	 */
	public StationArrival(Line line, Direction direction, Stop stop,
			Long timestamp, String vehicleId, boolean generated) {
		super();
		this.line = line;
		this.direction = direction;
		this.stop = stop;
		this.timestamp = timestamp;
		this.vehicleId = vehicleId;
		this.generated = generated;
	}
	
	/**
	 * @return the line
	 */
	public Line getLine() {
		return line;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @return the stop
	 */
	public Stop getStop() {
		return stop;
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the vehicleId
	 */
	public String getVehicleId() {
		return vehicleId;
	}

	public String toString() {
		return ("[" + timestamp + " ("
				+ ArrivalPredictor.formatTimestamp(timestamp) + "), "
				+ vehicleId + ", " + stop + "] " + generated);
	}

	@Override
	public int compareTo(StationArrival other) {
		// TODO Auto-generated method stub
		
		if (timestamp < other.timestamp)
			return (-1);
		if (timestamp > other.timestamp)
			return (1);
		return (0);
	}
}