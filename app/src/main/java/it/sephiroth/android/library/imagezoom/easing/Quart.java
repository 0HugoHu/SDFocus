package it.sephiroth.android.library.imagezoom.easing;

public class Quart implements Easing {

	 
	public double easeOut( double t, double b, double c, double d ) {
		return -c * ( ( t = t / d - 1 ) * t * t * t - 1 ) + b;
	}

	 
	public double easeIn( double t, double b, double c, double d ) {
		return c * ( t /= d ) * t * t * t + b;
	}

	 
	public double easeInOut( double t, double b, double c, double d ) {
		if ( ( t /= d / 2 ) < 1 ) return c / 2 * t * t * t * t + b;
		return -c / 2 * ( ( t -= 2 ) * t * t * t - 2 ) + b;
	}

}
