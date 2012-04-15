package network;

import java.util.Iterator;
import java.util.Map.Entry;

import main.LeechMain;

public class SCUpdateThread implements Runnable{
	Thread thread;
	LeechMain leech;
	Networking networking;
	
	public SCUpdateThread(LeechMain leech, Networking networking) {
		this.leech = leech;
		this.networking = networking;
		start();
	}
	
	//@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while(thread == thisThread) {
			float[] progresses = new float[leech.getTorrentFiles().size()];
			Iterator<Entry<String,Float>> iter = leech.getTorrentFiles().entrySet().iterator();
			int i=0;
			while(iter.hasNext()) {
				Entry<String,Float> entry = iter.next();
				progresses[i] = entry.getValue();
					i++;
			}
			networking.sendTorrentFiles(progresses);
			networking.sendDownloadDirectory(leech.getDownloadDirectory());
			
			sleep(100);
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
}
