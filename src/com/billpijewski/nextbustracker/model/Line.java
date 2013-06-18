package com.billpijewski.nextbustracker.model;

import java.util.Map;

import com.billpijewski.nextbustracker.errors.InvalidArgumentException;

public class Line {	
	private Map<String, Direction> directions;
	private String name;
	
	/**
	 * @param stops
	 * @param direction
	 * @param name
	 */
	public Line(Map<String, Direction> direction, String name) {
		super();
		this.directions = direction;
		this.name = name;
	}
	
	public Direction getDirection(String directionName) throws InvalidArgumentException {
		Direction direction = directions.get(directionName);

		if (direction == null) {
			throw new InvalidArgumentException("invalid direction for line "
					+ name + ": " + directionName);
		}

		return (directions.get(directionName));
	}
	
	public String getName() {
		return name;
	}
}