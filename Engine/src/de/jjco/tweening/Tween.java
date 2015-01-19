package de.jjco.tweening;

/**
 * The Tween class describes a function that returns a double between 0 and 1,
 * given the type of tweening, duration, and position in time.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 2 J
 */
public class Tween {
	public static final int LINEAR = 0;
	public static final int QUAD = 1;
	public static final int CUBIC = 2;
	public static final int QUART = 3;
	public static final int QUINT = 4;
	public static final int SINE = 5;
	public static final int EXPO = 6;
	public static final int CIRC = 7;
	public static final int ELASTIC = 8;
	public static final int BACK = 9;
	public static final int BOUNCE = 10;
	
	public static final int EASE_IN = 0;
	public static final int EASE_OUT = 1;
	public static final int EASE_IN_OUT = 2;
	public static final int EASE_OUT_IN = 3;
	
	/**
	 * Returns a double between 0 and 1,
     * given the type of tweening, duration, and position in time.
	 * 
	 * @param type the tween type
	 * @param easeType the easing type
	 * @param time the position in time
	 * @param duration the duration
	 * @return a number between 0 and 1 that fits the tween function
	 */
	public static double tween(int type, int easeType, double time, double duration) {
		double value = time / duration;
		
		switch (easeType) {
		case EASE_IN: return tween(type, value);
		case EASE_OUT: return 1 - tween(type, 1 - value);
		case EASE_IN_OUT: 
			if (value < 0.5) {
				return tween(type, value * 2) / 2;
			} else {
				return 1 - 0.5 * tween(type, 2 - value * 2);
			}
		case EASE_OUT_IN:
			if (value < .5) {
				return 0.5 - 0.5 * tween(type, 2 - value * 2);
			} else {
				return 0.5 + 0.5 * tween(type, value * 2 - 1);
			}
		default: throw new IllegalArgumentException("Incorrect ease type!");
		}
	}
	
	private static double tween(int type, double t) {
		switch (type) {
		case LINEAR: return t;
		case QUAD: return t * t;
		case CUBIC: return t * t * t;
		case QUART: return t * t * t * t;
		case QUINT: return t * t * t * t * t;
		case SINE: return -Math.cos(t * (Math.PI / 2)) + 1;
		case EXPO: return Math.pow(2, 10 * (t - 1)) - 0.001;
		case CIRC: return -(Math.sqrt(1 - t * t) - 1);
		case ELASTIC: return -(Math.pow(2, 10 * (t -= 1)) * Math.sin((t - (.3 / 4)) * (2 * Math.PI) / .3));
		case BACK: return t * t * (2.70158 * t - 1.70158);
		case BOUNCE: 
			t = 1 - t;
			
			if(t < 1 / 2.75) {
	            return 1 - (7.5625 * t * t);
	        } else if (t < 2 / 2.75) {
	            return 1 - (7.5625 * (t -= 1.5 / 2.75) * t + 0.75);
	        } else if (t < 2.5 / 2.75 ) {
	            return 1 - (7.5625 * (t -= 2.25 / 2.75 ) * t + 0.9375);
	        } else {
	            return 1 - (7.5625 * (t -= 2.625 / 2.75 ) * t + 0.984375);
	        }
		default: throw new IllegalArgumentException("Incorrect tween type!");
		}
	}
}