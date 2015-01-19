package de.jjco.tweening;

import java.lang.reflect.Field;

import de.jjco.EngineLog;

/**
 * A VarInterval is a type of interval that updates a field with a continual stream
 * of doubles that describes a tweening function over a certain time.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 2 J
 */
public class VarInterval extends Interval {
	private Object obj;
	private String var;
	
	private Field field;
	private double s;
	private double e;

	/**
	 * Creates a Variable Interval object that updates the variable with a continual stream
	 * of doubles that describes a tweening function over a certain time. The variable in the
	 * object can be private or protected, but it must be a double.
	 * 
	 * @param target	the object that contains the function
	 * @param field  	the variable you are updating
	 * @param start		the starting value of the tween
	 * @param end		the ending value of the tween
	 * @param duration	the tween's duration
	 * @param type		the type of tweening
	 * @param easing	the type of easing
	 * @see Tween
	 */
	public VarInterval(Object target, String field, double start, double end, long duration, int type, int easing) {
		super(duration, type, easing);
		
		obj = target;
		var = field;
		s = start;
		e = end;
	}

	@Override
	protected boolean animate(double val) {
		try {
			field.set(obj, s + (e - s) * val);
		} catch (IllegalAccessException e) {
			EngineLog.logException("Could not set the variable for " + this);
			EngineLog.logException(e);
		} catch (IllegalArgumentException e) {
			EngineLog.logException("Could not set the variable for " + this);
			EngineLog.logException(e);
		} 

		return (val == 1);
	}

	@Override
	protected boolean doStart() {
		try {
			// Get class of object
			Class<?> clazz = obj.getClass();
			if (obj instanceof Class<?>) {
				clazz = (Class<?>) obj;
			}

			// Get field in object
			Field f = null;
			try {
				f = clazz.getDeclaredField(var);
			} catch (NoSuchFieldException e) {
				f = clazz.getField(var);
			}

			if ( f != null ) {
				if ( !f.isAccessible() ) {
					f.setAccessible(true);
				}

				field = f;
			} 
		} catch (Exception e) {}
		return false;
	}
}
