package de.jjco.tweening;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a collection of Intervals that will be stepped/animated together.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 1
 * @since 1/18/2015
 */
public class IntervalManager {
	private static final IntervalManager BASE = new ThreadIntervalManager();
	
	private static IntervalManager def = BASE;
	private static ConcurrentLinkedQueue<Interval> intervals;
	
	/**
	 * Creates an Interval Manager
	 */
	public IntervalManager() {
		intervals = new ConcurrentLinkedQueue<Interval>();
	}
	
	/**
	 * Gets the default IntervalManager. 
	 * 
	 * @return the default
	 */
	public static IntervalManager getDefault() {
		return def;
	}
	
	/**
	 * Sets the default IntervalManager.
	 * 
	 * @param im
	 */
	public static void setDefault(IntervalManager im) {
		if (im == null) {
			im = BASE;
		}
		
		def = im;
	}
	
	/**
	 * Adds an interval to this manager.
	 * 
	 * @param i the interval to add
	 */
	public void add(Interval i) {
		if (i == null || intervals.contains(i)) {
			return;
		}
		
		intervals.add(i);
	}
	
	/**
	 * Steps each interval and removes them when done.
	 */
	public void animate() {
		for (Interval interval : intervals) {
			interval.step();
			if (interval.isDone()) {
				intervals.remove(interval);
			}
		}
	}
	
}
