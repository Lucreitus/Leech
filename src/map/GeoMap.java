package map;

import java.io.IOException;

import com.maxmind.geoip.*;

public class GeoMap {

	public static void getIP(String ip) {
		try {
		    LookupService cl = new LookupService("/usr/local/share/GeoIP/GeoLiteCity.dat",
			LookupService.GEOIP_MEMORY_CACHE );
	        Location l2 = cl.getLocation("213.52.50.8");
	        /*
		    System.out.println("countryCode: " + l2.countryCode +
	                               "\n countryName: " + l2.countryName +
	                               "\n region: " + l2.region +
	                               "\n regionName: " + regionName.regionNameByCode(l2.countryCode, l2.region) +
	                               "\n city: " + l2.city +
	                               "\n postalCode: " + l2.postalCode +
	                               "\n latitude: " + l2.latitude +
	                               "\n longitude: " + l2.longitude +
	 			       "\n metro code: " + l2.metro_code +
	 			       "\n area code: " + l2.area_code +
	                               "\n timezone: " + timeZone.timeZoneByCountryAndRegion(l2.countryCode, l2.region));

		    */
	        cl.close();
		}
		catch (IOException e) {
		    e.printStackTrace(System.out);
		}
	}
	
}
