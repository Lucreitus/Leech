package map;

import main.Global;

public class Convert {
	
	public static float lon(float lon) {
		float converted = 0.0f;
		converted = ((lon * Global.longToPixels) + Global.LONG_ZERO);
		//converted = converted - 15;
		return converted;
	}
	
	public static float lat(float lat) {		
		float converted = 0.0f;
		//converted = 90 - lat;
		//converted = (float) linCurve(converted,15,100,15,75,-4);
		//System.out.println(lat);
		//System.out.println(converted);
		//converted = ((converted * Global.longToPixels));
		//System.out.println(lat);
		converted = (Global.LAT_ZERO - (lat * Global.longToPixels));
		if(lat>=30 && lat<45) {
			converted = converted*0.9333f;
		} else if(lat>=45 && lat<60) {
			converted = converted*0.8625f;
		} else if(lat>=60) {
			converted = converted*0.8f;
		} else if(lat<= -30 && lat> -45) {
			converted = converted*1.077777f;
		} else if(lat<= -45 && lat> -60) {
			converted = converted*1.1375f;
		} else if(lat<= -60) {
			converted = converted*1.2f;
		}
		converted = converted + 52;
		return converted;
	}
	
	public static double linCurve(double value, double inMin, double inMax, double outMin, double outMax, double curve) {
		double grow, a, b, scaled = 0;
		
		grow = Math.exp(curve);
		a = outMax - outMin / (1.0 - grow);
		b = outMin + a;
		scaled = (value - inMin) / (inMax - inMin);
		
		return b - (a * Math.pow(grow, scaled));
	}
	
	public static double linlin(double value, double inMin, double inMax, double outMin, double outMax) {
		return (value-inMin)/(inMax-inMin) * (outMax-outMin) + outMin;
	}
}
