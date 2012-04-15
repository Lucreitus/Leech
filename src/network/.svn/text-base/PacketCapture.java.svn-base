package network;

import main.LeechMain;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PacketCapture implements PacketReceiver {
	  public void receivePacket(Packet packet) {
		  	//System.out.println(packet);
		  //LeechMain.processPacket(packet);
		  	//System.out.println(packet.datalink.toString());
			//System.out.println(packet.header[0]+"."+packet.header[1]+"."+packet.header[2]+"."+packet.header[3]);
			//System.out.println(new String(packet.data));
			//System.out.println(packet.EOF.toString());
		  

			//System.out.println(packet);
			String ip[] = packet.toString().split("/");
			//System.out.println(packet.sec);
			try{
				ip[2] = ip[2].split(" ")[0];
				//System.out.println(ip[2]);
				LeechMain.getNodes().get(ip[2]).sentPacket();
				LeechMain.getNetworking().sentPacket(LeechMain.getNodes().get("localHost").getRateUpload(), LeechMain.getNodes().get(ip[2]).getLatitude(), LeechMain.getNodes().get(ip[2]).getLongitude(), LeechMain.getNodes().get(ip[2]).getProgress(), LeechMain.getNodes().get("localHost").getProgress());
			} catch(Exception e) {
				
			}
			
			try{
				ip[1] = ip[1].substring(0, ip[1].length()-2);
				//System.out.println(ip[1]);
				LeechMain.getNodes().get(ip[1]).receivedPacket();
				LeechMain.getNetworking().receivedPacket(LeechMain.getNodes().get("localHost").getRateDownload(), LeechMain.getNodes().get(ip[1]).getLatitude(), LeechMain.getNodes().get(ip[1]).getLongitude(), LeechMain.getNodes().get(ip[1]).getProgress(), LeechMain.getNodes().get("localHost").getProgress());
			} catch(Exception e) {
				
			}
			/*
			if(packet.toString().contains("ARP REQUEST")) {
				String macAddress;
				String ipAddress = string.split
			}
			*/
		
		  
	  }
}