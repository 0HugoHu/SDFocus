package it.sephiroth.android.library.imagezoom.easing;

public class Linear implements Easing {

	public double easeNone( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	 
	public double easeOut( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	 
	public double easeIn( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	 
	public double easeInOut( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

}
