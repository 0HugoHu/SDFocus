package it.sephiroth.android.library.imagezoom.easing;

public class Sine implements Easing {

	 
	public double easeOut( double t, double b, double c, double d ) {
		return c * Math.sin( t / d * ( Math.PI / 2 ) ) + b;
	}

	 
	public double easeIn( double t, double b, double c, double d ) {
		return -c * Math.cos( t / d * ( Math.PI / 2 ) ) + c + b;
	}

	 
	public double easeInOut( double t, double b, double c, double d ) {
		return -c / 2 * ( Math.cos( Math.PI * t / d ) - 1 ) + b;
	}

}
