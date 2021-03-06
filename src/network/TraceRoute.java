package network;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.regionName;
import com.maxmind.geoip.timeZone;

import main.LeechMain;
import map.IPInfo;
import nodes.Node;
import nodes.RelayNode;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

public class TraceRoute implements Runnable{
	Thread thread;
	Node node;
	JpcapCaptor captor;
	ICMPPacket icmp;
	JpcapSender sender;
	LeechMain leech;
	private static short count = 0;

	public TraceRoute(Node node, LeechMain leech) {
		count++;
		this.node = node;
		this.leech = leech;
		captor = LeechMain.getCaptor();
		try {
			String address = node.getIP();
			//initialize Jpcap
			NetworkInterface device=JpcapCaptor.getDeviceList()[2];
			InetAddress thisIP=null;
			for(NetworkInterfaceAddress addr:device.addresses)
				if(addr.address instanceof Inet4Address){
					thisIP=addr.address;
					break;
				}

			//obtain MAC address of the default gateway
			//InetAddress pingAddr;
			//pingAddr = InetAddress.getByName("www.google.co.uk");

			//captor.setFilter("tcp and dst host "+pingAddr.getHostAddress(),true);
			/*
			byte[] gwmac=null;
			while(true){
				new URL("http://www.google.co.uk").openStream().close();
				Packet ping=captor.getPacket();
				if(ping==null){
					System.out.println("cannot obtain MAC address of default gateway.");
					System.exit(-1);
				}else if(Arrays.equals(((EthernetPacket)ping.datalink).dst_mac,device.mac_address))
					continue;
				gwmac=((EthernetPacket)ping.datalink).dst_mac;
				break;
			}
			*/

			//create ICMP packet
			icmp=new ICMPPacket();
			icmp.type=ICMPPacket.ICMP_ECHO;
			icmp.seq=count;
			icmp.id=count;
			icmp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,0,0,IPPacket.IPPROTO_ICMP,
					thisIP,InetAddress.getByName(address));
			icmp.data="data".getBytes();

			EthernetPacket ether=new EthernetPacket();
			ether.frametype=EthernetPacket.ETHERTYPE_IP;
			ether.src_mac=device.mac_address;
			ether.dst_mac=LeechMain.gwmac;
			icmp.datalink=ether;

			captor.setFilter("icmp and dst host "+thisIP.getHostAddress(),true);
			sender=captor.getJpcapSenderInstance();
			//JpcapSender sender=JpcapSender.openDevice(device);
			sender.sendPacket(icmp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while(thread == thisThread){
			ICMPPacket p=(ICMPPacket) captor.getPacket();
			//System.out.println(p);
			if(!(p==null)){
				//System.out.println("ID: "+ p.id + " src " + p.seq);
			}
			if(p==null){
				System.out.println("Timeout");
			}else if(p.type==ICMPPacket.ICMP_TIMXCEED){
				p.src_ip.getHostName();
				//System.out.println(icmp.hop_limit+": "+p.src_ip.getHostAddress());
				icmp.hop_limit++;
				addRelayNode(p);
			}else if(p.type==ICMPPacket.ICMP_UNREACH){
				p.src_ip.getHostName();
				//System.out.println(icmp.hop_limit+": "+p.src_ip.getHostAddress());
				addRelayNode(p);
				stop();
			}else if(p.type==ICMPPacket.ICMP_ECHOREPLY){
				p.src_ip.getHostName();
				System.out.println(icmp.hop_limit+": "+p.src_ip.getHostAddress());
				addRelayNode(p);
				stop();
			}else continue;
			sender.sendPacket(icmp);
		}
	}
	
	private void addRelayNode(ICMPPacket p) {
		System.out.println(p.src_ip.getHostAddress());
		if(!(
				p.src_ip.getHostAddress().contains("192.168.")||
				p.src_ip.getHostAddress().contains("10.219.") ||
				p.src_ip.getHostAddress().contains("127.0.0.1") ||
				p.src_ip.getHostAddress().contains("10.0.2")
		)) {
			node.addRelayNode(new RelayNode(p.src_ip.getHostAddress(),leech));	
		}
		    		 
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread = null;
	}
	
	 /**
     * Gets all matching dns records as an array of strings.
     *
     * @param domain domain, e.g. oberon.ark.com or oberon.com which you want
     *               the DNS records.
     * @param types  e.g."MX","A" to describe which types of record you want.
     *
     * @return ArrayList of Strings
     *
     * @throws NamingException if DNS lookup fails.
     */
    @SuppressWarnings ( {"PointlessBooleanExpression", "ConstantConditions"} )
    public static ArrayList<String> getDNSRecs( String domain,
                                                 String... types ) throws NamingException
        {
        ArrayList<String> results = new ArrayList<String>( 15 );

        //        Old Java 1.3 style required you to provide an explicit DNS server.
        //        DirContext ictx = new InitialDirContext();
        //        Attributes attrs =
        //                ictx.getAttributes( "dns://" + DNS_SERVER + "/" + domain,
        //                                    types );

        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put( "java.naming.factory.initial",
                 "com.sun.jndi.dns.DnsContextFactory" );
        DirContext ictx = new InitialDirContext( env );
        Attributes attrs = ictx.getAttributes( domain, types );
        for ( Enumeration e = attrs.getAll(); e.hasMoreElements(); )
            {
            Attribute a = (Attribute) e.nextElement();
            int size = a.size();
            for ( int i = 0; i < size; i++ )
                {
                // MX string has priority (lower better) followed by associated mailserver
                // A string is just IP
                results.add( (String) a.get( i ) );
                }// end inner for
            }// end  outer for
        if (results.size() == 0 )
            {
            System.err
                    .println( "Failed to find any DNS records for domain "
                              + domain );
            }
        return results;
        }
}
