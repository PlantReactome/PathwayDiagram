/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * Maintains a set of timers that you can use for benchmarking chunks of code.
 * Note that all methods are static.
 *
 * @author David Croft
 */
public class Timers {
	private HashMap<String,Long[]> timers = new HashMap<String,Long[]>();
	private boolean active = true;
	private String baseName = "";
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	/**
	 * Clears all currently existing timers
	 */
	public void clear() {
		if (!active)
			return;
		
		timers = new HashMap<String,Long[]>();
	}
	
	/**
	 * Starts off a timer, with an arbitrarily chosen, user-specified name.
	 * If a timer with this name has already been started, then this method
	 * call will overwrite the original start time..
	 * 
	 * @param timerName
	 */
	public void start(String timerName) {
		if (!active)
			return;
		
		Long[] timer = timers.get(timerName);
		if (timer == null) {
			timer = new Long[3];
			timer[1] = new Long(0);
			timer[2] = new Long(0);
		}
		timer[0] = new Long(System.currentTimeMillis());
		timers.put(timerName, timer);
	}
	
	/**
	 * Stops a timer, with an arbitrarily chosen, user-specified name.
	 * If a timer with this name does not exist, then this method
	 * call will be silently ignored.
	 * 
	 * Calling this method multiple times will increment the cumulative total
	 * of time recorded for timerName;
	 * 
	 * @param timerName
	 */
	public void stop(String timerName) {
		if (!active)
			return;
		
		Long[] timer = timers.get(timerName);
		if (timer != null) {
			timer[1] = elapsedTime(timerName);
			timer[2] = new Long(timer[2].longValue() + 1);
			timers.put(timerName, timer);
		}
	}
	
	public Long elapsedTime(String timerName) {
		if (!active)
			return null;
		
		Long[] timer = timers.get(timerName);
		if (timer != null)
			return new Long(timer[1].longValue() + System.currentTimeMillis() - timer[0].longValue());
		
		return null;
	}
	
	/**
	 * Prints elapsed times for all timers to STDOUT.
	 * 
	 * @param timerName
	 */
	public void printAll() {
		if (!active)
			return;
		
		printAll(System.out);
	}
	
	/**
	 * Prints elapsed times for all timers.
	 * 
	 * @param timerName
	 */
	public void printAll(PrintStream stream) {
		if (!active)
			return;
		
		stream.println(toString());
	}

	public String toString() {
		String string = "";
		
		if (!active)
			return string;
		
		string += "\n";
		string += "Process timers:\n";
		Object[] timerNames = timers.keySet().toArray();
		Arrays.sort(timerNames);
		for (Object timerName: timerNames)
			string += timeToString((String) timerName);
		string += "\n";
		
		return string;
	}
	
	/**
	 * Prints elapsed time for a timer, with an arbitrarily chosen, user-specified name.
	 * If a timer with this name does not exist, then this method
	 * call will be silently ignored.
	 * 
	 * @param timerName
	 */
	public String timeToString(String timerName) {
		Long[] timer = timers.get(timerName);
		String string = "";
		
		if (!active)
			return string;
		
		if (timer != null)
			string += "    " + baseName + timerName + ": " + timer[1] + " milliseconds, " + timer[2] + " calls";
		else
			string += "    Unknown timer: " + timerName;
		string += "\n";
		
		return string;
	}
}
