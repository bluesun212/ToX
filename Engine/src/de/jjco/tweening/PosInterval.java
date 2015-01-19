package de.jjco.tweening;

import de.jjco.components.CompNode;

/**
 * A PosInterval is a type of interval that moves a component over time.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 2 J
 */
public class PosInterval extends Interval {
	private CompNode comp;
	private double startX;
	private double startY;
	private double endX;
	private double endY;
	
	/**
	 * Creates a Positional Interval object that moves the object over a certain time.
	 * 
	 * @param c			the component that is being moved
	 * @param ex		the ending X coordinate
	 * @param ey 		the ending Y coordinate
	 * @param duration	the tween's duration
	 * @param type		the type of tweening
	 * @param easing	the type of easing
	 * @see Tween
	 */
	public PosInterval(CompNode c, double ex, double ey, long duration, int type, int easing) {
		super(duration, type, easing);
		
		comp = c;
		endX = ex;
		endY = ey;
	}

	@Override
	protected boolean animate(double val) {
		comp.setX(startX + (endX - startX) * val);
		comp.setY(startY + (endY - startY) * val);
		return (val == 1);
	}

	@Override
	protected boolean doStart() {
		startX = comp.getX();
		startY = comp.getY();
		return (true);
	}
}
