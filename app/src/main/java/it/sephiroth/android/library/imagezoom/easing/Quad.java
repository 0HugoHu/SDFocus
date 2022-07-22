package it.sephiroth.android.library.imagezoom.easing;

public class Quad implements Easing {

	 
	public double easeOut( double t, double b, double c, double d ) {
		return -c * ( t /= d ) * ( t - 2 ) + b;
	}

	 
	public double easeIn( double t, double b, double c, double d ) {
		return c * ( t /= d ) * t + b;
	}

	 
	public double easeInOut( double t, double b, double c, double d ) {
		if ( ( t /= d / 2 ) < 1 ) return c / 2 * t * t + b;
		return -c / 2 * ( ( --t ) * ( t - 2 ) - 1 ) + b;
	}

}
