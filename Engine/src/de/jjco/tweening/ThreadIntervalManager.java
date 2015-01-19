package de.jjco.tweening;

/**
 * A Thread where an interval manager is stepped.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 1
 * @since 1/18/2015
 */
public class ThreadIntervalManager extends IntervalManager implements Runnable {
	private volatile boolean running;
	
	/**
	 * Creates a threaded interval manager.
	 */
	public ThreadIntervalManager() {
		this("ToXicity Thread Interval Manager");
	}
	
	/**
	 * Creates a threaded interval manager with the specified name.
	 * 
	 * @param name the name
	 */
	public ThreadIntervalManager(String name) {
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.setName(name);
		t.start();
	}
	
	/**
	 * Stops the thread.
	 */
	public void stop() {
		running = false;
	}
	
	@Override
	public void run() {
		running = true;
		
		while (running) {
			animate();
			
			try {
				Thread.sleep(1l);
			} catch (InterruptedException e) {}
		}
	}
	
}
