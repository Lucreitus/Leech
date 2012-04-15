package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import utils.Base64;

import nodes.LeechNode;
import nodes.TrackerNode;

import main.LeechMain;

public class TorrentControl implements Runnable{
	LeechMain leech;
	String url = "http://127.0.0.1:9091/transmission/rpc";
	URL bitTorrent;
	URLConnection bit;
	OutputStreamWriter wr;
	String sessionID;
	Thread thread;
	BufferedReader rd;
	public static final int START = 0;
	public static final int STOP = 1;
	public static final int PEERS = 2;
	public static final int DOWNLOAD_LIMIT = 3;
	public static final int DOWNLOAD_LIMITED = 4;
	public static final int UPLOAD_LIMIT = 5;
	public static final int UPLOAD_LIMITED = 6;
	public static final int PEER_LIMIT = 7;
	public static final int TRACKER = 8;
	public static final int DOWNLOAD_DIRECTORY = 9;
	public static final int PRIORITY_LOW = 10;
	public static final int PRIORITY_HIGH = 11;
	public static final int DELETE_LOCAL_DATA = 12;
	public static final int LOAD_TORRENT = 13;
	
	private float downloadLimit = 10;
	private float peerLimit = 200;
	int[] priorityArray;
	boolean prioritySet = true;
	boolean directory = false;
	
	public TorrentControl(LeechMain leech) {
		this.leech = leech;
		//initComm();
		//postRequest(DOWNLOAD_LIMITED);
		//readComm();
		//initComm();
		//postRequest(UPLOAD_LIMITED);
		//readComm();
		initComm();
		postRequest(TRACKER);
		readComm();
		
		initComm();
		postRequest(DOWNLOAD_DIRECTORY);
		readComm();
		
		initComm();
		postRequest(PEER_LIMIT);
		readComm();
		
		initComm();
		postRequest(START);
		readComm();
		start();
	}
	
	//@Override
	public void run() {	
		Thread thisThread = Thread.currentThread();		
		while(thread == thisThread) {
			initComm();
			postRequest(PEERS);
			readComm();
			sleep(200);
		}
	}
	
	public void initComm() {
		try {
			bitTorrent = new URL(url);
			bit = bitTorrent.openConnection();
			sessionID = bit.getHeaderField("X-Transmission-Session-Id");
			bit = bitTorrent.openConnection();	
			bit.setAllowUserInteraction(true);
			bit.setRequestProperty("X-Transmission-Session-Id", sessionID);
			bit.setDoInput(true);
			bit.setDoOutput(true);
			wr = new OutputStreamWriter(bit.getOutputStream());     	
		} catch (MalformedURLException e) {
			e.printStackTrace(System.out);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	private void postRequest(int bitCom) {
		 // Construct data
		try {
			switch(bitCom) {
				case DELETE_LOCAL_DATA:
					wr.write("{\"arguments\":{\"delete-local-data\":\"true\"},\"method\":\"torrent-remove\",\"tag\":1300}");
					break;
				case LOAD_TORRENT:
					wr.write("{\"arguments\":{\"filename\":\"/Users/curtmangz/Desktop/leech torrents/leech.torrent\"},\"method\":\"torrent-add\",\"tag\":1300}");
					break;
				case START:
					wr.write("{\"method\":\"torrent-start\",\"tag\":100}");
					break;
				case STOP:
					wr.write("{\"method\":\"torrent-stop\",\"tag\":200}");
					break;
				case PEERS:
					wr.write("{\"arguments\":{\"fields\":[\"name\",\"totalSize\",\"pieces\",\"percentDone\",\"peers\",\"rateDownload\",\"rateUpload\",\"files\",\"priorities\"]},\"method\":\"torrent-get\",\"tag\":300}");
					break;
				case DOWNLOAD_LIMIT:
					wr.write("{\"arguments\":{\"downloadLimit\":"+downloadLimit+"},\"method\":\"torrent-set\",\"tag\":400}");
					break;
				case DOWNLOAD_LIMITED:
					wr.write("{\"arguments\":{\"downloadLimited\":true},\"method\":\"torrent-set\",\"tag\":500}");
					break;
				case UPLOAD_LIMIT:
					wr.write("{\"arguments\":{\"uploadLimit\":"+downloadLimit+"},\"method\":\"torrent-set\",\"tag\":600}");
					break;
				case UPLOAD_LIMITED:
					wr.write("{\"arguments\":{\"uploadLimited\":true},\"method\":\"torrent-set\",\"tag\":700}");
					break;
				case PEER_LIMIT:
					wr.write("{\"arguments\":{\"peer-limit\":"+peerLimit+"},\"method\":\"torrent-set\",\"tag\":800}");
					break;
				case TRACKER:
					wr.write("{\"arguments\":{\"fields\":[\"trackers\"]},\"method\":\"torrent-get\",\"tag\":900}");
					break;
				case DOWNLOAD_DIRECTORY:
					wr.write("{\"arguments\":{\"fields\":[\"downloadDir\"]},\"method\":\"torrent-get\",\"tag\":1000}");
					break;
				case PRIORITY_LOW:
					String priorityString = "[";
					for(int i=0;i<priorityArray.length-1;i++) {
						priorityString = priorityString+i+",";
					}
					priorityString = priorityString+(priorityArray.length-1)+"]";
					System.out.println(priorityString);
					wr.write("{\"arguments\":{\"priority-low\":"+priorityString+"},\"method\":\"torrent-set\",\"tag\":1100}");
					break;
				case PRIORITY_HIGH:
					priorityString = "[0,1,2]";
					wr.write("{\"arguments\":{\"priority-high\":"+priorityString+"},\"method\":\"torrent-set\",\"tag\":1200}");
					break;
			}
		    wr.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(System.out);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void readComm() {
		try {
			rd = new BufferedReader(new InputStreamReader(bit.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace(System.out);
		}
		try {
			String post;
			while ((post = rd.readLine()) != null) {
				parsePost(post);
			}
			wr.close();
		    rd.close();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		thread = null;
	}
	
	private void sleep(int millis) {
		try {
			Thread.currentThread().sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	
	public void parsePost(String post) {
		//System.out.println(post);
		String[] lines = post.split("\"percentDone\":");
		//System.out.println(lines[1]);
		if(post.contains("\"percentDone\":")) {
				leech.getNodes().get("localHost").setProgress(
						new Float(lines[1].split(",\"pieces\":")[0])
				);
				lines = post.split("\"rateUpload\":");
				leech.getNodes().get("localHost").setRateUpload(
						new Float(lines[1].split(",\"totalSize\":")[0])
				);
				lines = post.split("\"rateDownload\":");
				leech.getNodes().get("localHost").setRateDownload(
						new Float(lines[1].split(",\"rateUpload\":")[0])
				);
				lines = post.split("\"pieces\":");
				leech.getNodes().get("localHost").setPieces(
						Base64.decode(lines[1].split(",\"rateDownload\":")[0])
				);
				//for(int i2=0;i2<leech.getNodes().get("localHost").getPieces().length;i2++) {
				//	System.out.println(leech.getNodes().get("localHost").getPieces()[i2]);
				//}
				lines = post.split("\"pieces\":");
				leech.getNodes().get("localHost").setPieces(
						Base64.decode(lines[1].split(",\"rateDownload\":")[0])
				);
				lines = post.split("\"files\":");
				String[] sublines = lines[1].split("\"bytesCompleted\":");
				priorityArray = new int[sublines.length-1];
				for(int i=1;i<sublines.length;i++) {
					if(i<4) {
						priorityArray[i-1] = 1;
					} else {
						priorityArray[i-1] = -1;
					}
					String[] subSubline = sublines[i].split("\"bytesCompleted\":");
					String bytesCompleted = sublines[i].split(",\"length\":")[0];
					subSubline = sublines[i].split("\"length\":");
					String length = subSubline[1].split(",\"name\":\"")[0];
					float percentCompleted = new Float(bytesCompleted) / new Float(length);
					subSubline = sublines[i].split("\"name\":\"");
					String name = subSubline[1].split("\"}")[0];
					
					
					if(leech.getTorrentFiles().size() < 300) {
						if(!leech.getTorrentFiles().containsKey(name)) {
							if(name.contains(".mp3")) {
								leech.getTorrentFiles().put(name, percentCompleted);
							}
						} else {
							if(percentCompleted==1) {
								
								if(leech.getTorrentFiles().get(name)<1) {
									if(name.contains(".mp3")) {
										//leech.getNetworking().sendMp3(leech.getNodes().get("localHost").getRateUpload(),leech.getNodes().get("localHost").getRateDownload(),leech.getNodes().get("localHost").getProgress(),name);
									}
								}
							}
							leech.getTorrentFiles().replace(name, percentCompleted);
						}
					}
				}
			if(!prioritySet) {
				initComm();
				postRequest(PRIORITY_LOW);
				readComm();
				initComm();
				postRequest(PRIORITY_HIGH);
				readComm();
				prioritySet = true;
			}
		}
		lines = post.split("\"address\"");
		for(int i=2;i<lines.length;i++) {
			String[] sublines = lines[i].split("\"");
			if(!leech.getNodes().containsKey(sublines[1])) {
				//System.out.println(sublines[1]);
				leech.getNodes().put(sublines[1],new LeechNode(sublines[1],leech));
			}
			String[] sublines2 = lines[i].split(",");
			leech.getNodes().get(sublines[1]).setProgress(new Float(sublines2[12].split(":")[1]));
		}
		
		try{
			lines = post.split("\"name\":");
			lines = lines[1].split(",");
			leech.setName(lines[0]);
		} catch(Exception e) {
			
		}
		
		if(post.contains("\"trackers\"")) {
			lines = post.split("\"announce\":\"http://");
			for(int i=1;i<lines.length;i++) {
				String[] sublines = lines[i].split(":");
				sublines = sublines[0].split("/");
				
				String addr = null;
				/*
				try {
					addr = (String) TraceRoute.getDNSRecs(sublines[0], "A").get(0);
					System.out.println(sublines[0]);
					System.out.println(addr);
					if(!leech.getNodes().containsKey(addr)) {
						leech.getNodes().put(addr,new TrackerNode(addr,leech,sublines[0]));
					}
				} catch (NamingException e) {
					e.printStackTrace(System.out);
				}
				*/			
			}
		}
		
		if(post.contains("\"downloadDir\"")) {
			lines = post.split("\"downloadDir\":\"");
			String downloadDirectory = lines[1].split("\"}]}")[0];
			System.out.println(downloadDirectory);
			leech.setDownloadDirectory(downloadDirectory);
			if(!directory) {
				try{
					
					boolean success = (new File(downloadDirectory+"/unfinished")).mkdir();
					if (success) {
						System.out.println("Directory: " + downloadDirectory+"/unfinished" + " created");
					} else {
						System.out.println("wtf?");
						System.out.println("wtf?");
						System.out.println("wtf?");
					}
					directory = true;
			    }catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			    }
			  
			}
		}
	}
	
	public void startTorrent() {
		initComm();
		postRequest(START);
		readComm();
	}
	
	public void stopTorrent() {
		initComm();
		postRequest(STOP);
		readComm();
	}
	
	public float getDownloadLimit() {
		return downloadLimit;
	}
	public void setDownloadLimit(float downloadLimit) {
		this.downloadLimit = downloadLimit;
		initComm();
		postRequest(DOWNLOAD_LIMIT);
		readComm();
		initComm();
		postRequest(UPLOAD_LIMIT);
		readComm();
	}
	public float getPeerLimit() {
		return peerLimit;
	}
	public void setPeerLimit(float peerLimit) {
		this.peerLimit = peerLimit;
		initComm();
		postRequest(PEER_LIMIT);
		readComm();
	}
	
	public void deleteData() {
		initComm();
		postRequest(DELETE_LOCAL_DATA);
		readComm();
	}
	
	public void resetTorrent() {
		
		initComm();
		postRequest(LOAD_TORRENT);
		readComm();
	}
}