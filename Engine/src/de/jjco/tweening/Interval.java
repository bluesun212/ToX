package de.jjco.tweening;

import de.jjco.EngineLog;

/**
 * An interval is an object that represents a change over a given period of time.  
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 1
 * @since 1/18/2015
 */
public abstract class Interval {
	private long startTime;
	private long duration;
	private int type;
	private int easeType;
	private int state;
	
	/**
	 * Creates an interval over the specified duration using the specified type of tweening.
	 * 
	 * @param duration the duration
	 * @param type tweening type
	 * @param easeType easing type
	 * @see Tween
	 */
	public Interval(long duration, int type, int easeType) {
		this.duration = duration;
		this.easeType = easeType;
		this.type = type;
		state = 0;
	}
	
	/**
	 * Starts the interval using the default Interval Manager
	 * 
	 * @return itself
	 */
	public Interval start() {
		return start(IntervalManager.getDefault());
	}
	
	/**
	 * Starts the interval using the specified Interval Manager
	 * 
	 * @param im the interval manager
	 * @return itself
	 */
	public Interval start(IntervalManager im) {
		startTime = System.currentTimeMillis();
		if (tryStart()) {
			im.add(this);
			EngineLog.log("Starting " + this);
			state = 1;
		} else {
			EngineLog.log("Could not start " + this);
			state = 0;
		}
		
		return this;
	}
	
	private boolean tryStart() {
		try {
			return doStart();
		} catch (Exception e) {}
		
		return false;
	}
	
	/**
	 * Sleeps the thread until this interval is finished tweening.
	 */
	public void spin() {
		while (state == 1) {
			try {
				Thread.sleep(1l);
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Checks to see if this interval has started but hasn't finished.
	 * 
	 * @return if done
	 */
	public boolean isDone() {
		return state == 2;
	}
	
	/**
	 * Updates this interval.
	 */
	void step() {
		if (state != 1) {
			return;
		}
		
		double time = Math.min(Math.max(System.currentTimeMillis() - startTime, 0), duration);
		if (animate(Tween.tween(type, easeType, time, duration))) {
			state = 2;
		}
	}
	
	/**
	 * Updates this interval with the specified tween value between 0 and 1.
	 * 
	 * @param val the tweening value
	 * @return if the interval is done
	 */
	protected abstract boolean animate(double val);
	
	/**
	 * Starts this interval.
	 * 
	 * @return if the interval started successfully
	 */
	protected abstract boolean doStart();
}
