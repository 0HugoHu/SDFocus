package it.sephiroth.android.library.imagezoom.easing;

public class Cubic implements Easing {

	 
	public double easeOut( double time, double start, double end, double duration ) {
		return end * ( ( time = time / duration - 1.0 ) * time * time + 1.0 ) + start;
	}

	 
	public double easeIn( double time, double start, double end, double duration ) {
		return end * ( time /= duration ) * time * time + start;
	}

	 
	public double easeInOut( double time, double start, double end, double duration ) {
		if ( ( time /= duration / 2.0 ) < 1.0 ) return end / 2.0 * time * time * time + start;
		return end / 2.0 * ( ( time -= 2.0 ) * time * time + 2.0 ) + start;
	}
}
