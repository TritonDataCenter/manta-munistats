package com.billpijewski.nextbustracker.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.billpijewski.nextbustracker.model.Direction;
import com.billpijewski.nextbustracker.model.Line;
import com.billpijewski.nextbustracker.model.Stop;

/**
 * Read an XML configuration file for a transit line, which will contain two or
 * more routes.
 * 
 * @author Bill Pijewski
 */
final class LineReader {

	private String agency;
	private String route;

	public LineReader(String agency, String route) {
		super();

		this.agency = agency;
		this.route = route;
	}

	public Line readLineConfig() throws JDOMException, IOException {
		String lineConfig = "./routes/" + agency + "/" + route + ".xml";

		File file = new File(lineConfig);
		FileReader reader = new FileReader(file);

		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(reader);

		Element root = document.getRootElement();
		Element body = root.getChild("route");
		String tag = body.getAttributeValue("tag");

		Map<String, Stop> stopsByTag = readLineStops(body);
		Map<String, Direction> routes = readLineDirections(body, stopsByTag);

		Line line = new Line(routes, tag);

		return (line);
	}

	private Map<String, Stop> readLineStops(Element body) {
		List<Element> stops = body.getChildren("stop");
		Map<String, Stop> stopsByTag = new HashMap<String, Stop>();

		for (Element elem : stops) {
			String tag = elem.getAttributeValue("tag");
			String title = elem.getAttributeValue("title");
			Float lat = Float.parseFloat(elem.getAttributeValue("lat"));
			Float lon = Float.parseFloat(elem.getAttributeValue("lon"));
			String stopId = elem.getAttributeValue("stopId");

			Stop stop = new Stop(tag, title, lat, lon, stopId);
			stopsByTag.put(tag, stop);
		}

		return (stopsByTag);
	}

	private Map<String, Direction> readLineDirections(Element body,
			Map<String, Stop> existingStops) {
		Map<String, Direction> routes = new HashMap<String, Direction>();

		for (Element elem : body.getChildren("direction")) {
			List<Stop> stops = readDirectionStops(elem, existingStops);
			String title = elem.getAttributeValue("title");
			String name = elem.getAttributeValue("name");
			String tag = elem.getAttributeValue("tag");

			routes.put(tag, new Direction(stops, title, name, tag));
		}

		return (routes);
	}

	private List<Stop> readDirectionStops(Element route,
			Map<String, Stop> existingStops) {
		List<Stop> stops = new LinkedList<Stop>();

		for (Element elem : route.getChildren("stop")) {
			String tag = elem.getAttributeValue("tag");
			Stop stop = existingStops.get(tag);

			if (stop == null) {
				System.err.println("Error: no stop for tag " + tag);
				continue;
			}

			stops.add(stop);
		}

		return (stops);
	}
}