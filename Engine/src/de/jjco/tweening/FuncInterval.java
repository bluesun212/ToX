package de.jjco.tweening;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.jjco.EngineLog;

/**
 * A FuncInterval is a type of interval that updates a function with a continual stream
 * of doubles that describes a tweening function over a certain time.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 2 J
 */
public class FuncInterval extends Interval {
	private Object obj;
	private String method;

	private Method meth;
	private double s;
	private double e;

	/**
	 * Creates a Function Interval object that updates the function with a continual stream
	 * of doubles that describes a tweening function over a certain time. The method in the
	 * object can be private or protected, but it must have only one argument, and the
	 * argument must be a double.
	 * 
	 * @param target	the object that contains the function
	 * @param meth		the method you are updating
	 * @param start		the starting value of the tween
	 * @param end		the ending value of the tween
	 * @param duration	the tween's duration
	 * @param type		the type of tweening
	 * @param easing	the type of easing
	 * @see Tween
	 */
	public FuncInterval(Object target, String meth, double start, double end, long duration, int type, int easing) {
		super(duration, type, easing);
		
		obj = target;
		method = meth;
		s = start;
		e = end;
	}

	@Override
	protected boolean animate(double val) {
		try {
			meth.invoke(obj, s + ((e - s) * val));
		} catch (IllegalAccessException e) {
			EngineLog.logException("Could not invoke the method for " + this);
			EngineLog.logException(e);
		} catch (IllegalArgumentException e) {
			EngineLog.logException("Could not invoke the method for " + this);
			EngineLog.logException(e);
		} catch (InvocationTargetException e) {
			EngineLog.logException("Could not invoke the method for " + this);
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

			// Get method
			Method m = null;
			try {
				m = clazz.getDeclaredMethod(method, new Class<?>[]{double.class});
			} catch (NoSuchMethodException e) {
				m = clazz.getMethod(method, new Class<?>[]{double.class});
			}

			if (m != null) {
				if (!m.isAccessible()) {
					m.setAccessible(true);
				}

				meth = m;
				return true;
			} 
		} catch (Exception e) {}
		return (false);
	}
}
