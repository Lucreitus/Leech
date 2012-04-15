package map;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.regionName;
import com.maxmind.geoip.timeZone;

import main.LeechMain;
import nodes.Node;

public class IPInfo implements Runnable{
	private Thread thread;
	String ip;
	private Node node;
	Location l1;
	LeechMain leech;

	public IPInfo(Node node, LeechMain leech) {
		this.node = node;
		this.ip = node.getIP();
		this.leech = leech;
		this.start();
	}

	//@Override
	public void run() {
		try{
			Location l1 = leech.cl.getLocation(ip);
			/*
		System.out.println("countryCode: " + l1.countryCode +
				"\n countryName: " + l1.countryName +
				"\n region: " + l1.region +
				"\n regionName: " + regionName.regionNameByCode(l1.countryCode, l1.region) +
				"\n city: " + l1.city +
				"\n postalCode: " + l1.postalCode +
				"\n latitude: " + l1.latitude +
				"\n longitude: " + l1.longitude +
				"\n metro code: " + l1.metro_code +
				"\n area code: " + l1.area_code +
				"\n timezone: " + timeZone.timeZoneByCountryAndRegion(l1.countryCode, l1.region)
		);
		*/
			node.setLatitude(l1.latitude);
			node.setLongitude(l1.longitude);
			node.setInitiated(true);
		} catch(Exception e) {
			
		}
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread = null;
	}
}
