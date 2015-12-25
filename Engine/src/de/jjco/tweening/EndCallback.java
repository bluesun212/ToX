package de.jjco.tweening;

/**
 * An EndCallback is the class that contains the function
 * called when an Interval finishes animating.
 * @author Jared Jonas (bluesun212)
 * @version Revision 1 
 * @since 12/25/2015
 */
public interface EndCallback {
	
	/**
	 * Called when an interval finishes animating. 
	 * 
	 * @param ival the interval
	 */
	public abstract void onFinish(Interval ival);
}
