package network;

import java.net.Inet4Address;
import java.net.InetAddress;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

public class PacketCap implements Runnable{
	private JpcapCaptor captor;
	private Thread thread;
	InetAddress thisIP=null;
	
	public PacketCap(JpcapCaptor captor) {
		this.captor = captor;		
		start();
	}

	//@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		
		NetworkInterface device = JpcapCaptor.getDeviceList()[2];
		//for(int i=0;i<JpcapCaptor.getDeviceList().length;i++) {
			//System.out.println()
		//}
		//NetworkInterface device = JpcapCaptor.getDeviceList()[0];
		try {
			captor = JpcapCaptor.openDevice(device,2000,false,5000);
			//captor.setFilter("icmp",false);
			//System.out.println(captor.)
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
		captor.setNonBlockingMode(true);
		while(thread==thisThread) {
			try{
				captor.processPacket(-1,new PacketCapture());
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
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
