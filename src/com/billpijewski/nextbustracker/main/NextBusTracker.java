package com.billpijewski.nextbustracker.main;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Bill Pijewski
 */
public class NextBusTracker {
	final static String HOST = "webservices.nextbus.com";
	final static String FEED_PATH = "/service/publicXMLFeed";
	final static String AGENCY = "sf-muni";
	
	final static String OUTPUT_DIR = "data/";
	
	public static void main(String[] args) {
		List<Thread> threads = new LinkedList<Thread>();
		
		threads.add(new LineQueryer("F"));
		threads.add(new LineQueryer("J"));
		threads.add(new LineQueryer("KT"));
		threads.add(new LineQueryer("L"));
		threads.add(new LineQueryer("M"));
		threads.add(new LineQueryer("N"));
		threads.add(new LineQueryer("NX"));
		threads.add(new LineQueryer("1"));
		threads.add(new LineQueryer("1AX"));
		threads.add(new LineQueryer("1BX"));
		threads.add(new LineQueryer("2"));
		threads.add(new LineQueryer("3"));
		threads.add(new LineQueryer("5"));
		threads.add(new LineQueryer("6"));
		threads.add(new LineQueryer("8X"));
		threads.add(new LineQueryer("8AX"));
		threads.add(new LineQueryer("8BX"));
		threads.add(new LineQueryer("9"));
		threads.add(new LineQueryer("9L"));
		threads.add(new LineQueryer("10"));
		threads.add(new LineQueryer("12"));
		threads.add(new LineQueryer("14"));
		threads.add(new LineQueryer("14L"));
		threads.add(new LineQueryer("14X"));
		threads.add(new LineQueryer("16X"));
		threads.add(new LineQueryer("17"));
		threads.add(new LineQueryer("18"));
		threads.add(new LineQueryer("19"));
		threads.add(new LineQueryer("21"));
		threads.add(new LineQueryer("22"));
		threads.add(new LineQueryer("23"));
		threads.add(new LineQueryer("24"));
		threads.add(new LineQueryer("27"));
		threads.add(new LineQueryer("28"));
		threads.add(new LineQueryer("28L"));
		threads.add(new LineQueryer("29"));
		threads.add(new LineQueryer("30"));
		threads.add(new LineQueryer("30X"));
		threads.add(new LineQueryer("31"));
		threads.add(new LineQueryer("31AX"));
		threads.add(new LineQueryer("31BX"));
		threads.add(new LineQueryer("33"));
		threads.add(new LineQueryer("35"));
		threads.add(new LineQueryer("36"));
		threads.add(new LineQueryer("37"));
		threads.add(new LineQueryer("38"));
		threads.add(new LineQueryer("38AX"));
		threads.add(new LineQueryer("38BX"));
		threads.add(new LineQueryer("38L"));
		threads.add(new LineQueryer("39"));
		threads.add(new LineQueryer("41"));
		threads.add(new LineQueryer("43"));
		threads.add(new LineQueryer("44"));
		threads.add(new LineQueryer("45"));
		threads.add(new LineQueryer("47"));
		threads.add(new LineQueryer("48"));
		threads.add(new LineQueryer("49"));
		threads.add(new LineQueryer("52"));
		threads.add(new LineQueryer("54"));
		threads.add(new LineQueryer("56"));
		threads.add(new LineQueryer("66"));
		threads.add(new LineQueryer("67"));
		threads.add(new LineQueryer("71"));
		threads.add(new LineQueryer("71L"));
		threads.add(new LineQueryer("76X"));
		threads.add(new LineQueryer("80X"));
		threads.add(new LineQueryer("81X"));
		threads.add(new LineQueryer("82X"));
		threads.add(new LineQueryer("83X"));
		threads.add(new LineQueryer("88"));
		threads.add(new LineQueryer("90"));
		threads.add(new LineQueryer("91"));
		threads.add(new LineQueryer("108"));
		threads.add(new LineQueryer("K OWL"));
		threads.add(new LineQueryer("L OWL"));
		threads.add(new LineQueryer("M OWL"));
		threads.add(new LineQueryer("N OWL"));
		threads.add(new LineQueryer("T OWL"));
		threads.add(new LineQueryer("59"));
		threads.add(new LineQueryer("60"));
		threads.add(new LineQueryer("61"));		
		
		for (Thread t : threads) {
			t.start();
		}	
	}
}
