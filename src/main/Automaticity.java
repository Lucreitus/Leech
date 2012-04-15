package main;

import java.util.Iterator;
import java.util.Map.Entry;

import nodes.Node;

public class Automaticity implements Runnable{
	Thread thread;
	int[] songs;
	int numSongs;
	LeechMain leech;
	int song1,song2;
	int counter;
	
	public Automaticity(LeechMain leech) {
		this.leech = leech;
		songs = new int[2];
		songs[0] = 0;
		songs[1] = 0;
		numSongs = 0;
		song1 = 0;
		song2 = 12;
		counter = 0;
		start();
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while(thread == thisThread){
			if(leech.getNodes().get("localHost").getProgress()*100 >= 100) {
				counter = 0;
				numSongs = 0;
				leech.restart();
			} else {
				counter+=1;
				System.out.println(counter);
				if(numSongs<1) {
					if(counter >=30) {
						playSong();
						counter = 0;
					}
				} else {
					if(counter>=150) {
						playSong();
						counter = 0;
					}
				}
			}
						
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			}

		}	
	}
	
	public void playSong() {
		if(numSongs==0) {
			song1  = (song1+1)%12;
			song2  = (song2-1)%12;
			if(song2 < 0) {
				song2 = 12;
			}
			if(song1==song2) {
				song1  = (song1+1)%12;
			}
			songs[0] = song1;
			numSongs = 1;
			startSong(song1);
		} else if(numSongs==1) {
			song1  = (song1+1)%12;
			song2  = (song2-1)%12;
			if(song2 < 0) {
				song2 = 12;
			}
			if(song1==song2) {
				song1  = (song1+1)%12;
			}
			numSongs = 2;
			songs[1] = songs[0];
			songs[0] = song2;
			
			startSong(song2);
		} else if(numSongs==2) {
			song1  = (song1+1)%12;
			song2  = (song2-1)%12;
			if(song2 < 0) {
				song2 = 12;
			}
			if(song1==song2) {
				song1  = (song1+1)%12;
			}
			
			int choice = (int) (Math.random()*3);
			switch(choice) {
				case 0:
					stopSong(songs[1]);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					songs[1] = songs[0];
					songs[0] = song1;
					startSong(song1);
					numSongs = 2;
					break;
				case 1:
					stopSong(songs[1]);
					songs[1] = songs[0];
					numSongs = 1;
					break;
			}
			
			
		} else if(numSongs>2) {
			song1  = (song1+1)%12;
			song2  = (song2-1)%12;
			if(song2 < 0) {
				song2 = 12;
			}
			if(song1==song2) {
				song1  = (song1+1)%12;
			}
			stopSong(songs[1]);
		}		
	}
	
	public void startSong(int song) {
		if(song > -1) {
			leech.stats.playSong(song);
			
			Iterator<Entry<String,Float>> iter = leech.getTorrentFiles().entrySet().iterator();
			String name = "nothing";
			boolean complete = false;
			int i=0;
			while(iter.hasNext()) {
				//System.out.println(i);
				Entry<String,Float> entry = iter.next();
				if(entry.getKey().contains(".mp3")) {
					if(i == song) {
						name = entry.getKey();
						if(entry.getValue()>=1) {
							complete = true;
						}
					}
					i++;
				}
			}
			System.out.println(name);
			String subName = name.split("/")[1];
			leech.getNetworking().sendMp3(leech.getNodes().get("localHost").getRateUpload(),leech.getNodes().get("localHost").getRateDownload(),leech.getNodes().get("localHost").getProgress(),"/unfinished/wavs/"+subName,song);
		}
	}
	
	public void resendSongs() {
		if(numSongs==1) {
			startSong(songs[0]);
		} else if(numSongs==2) {
			startSong(songs[0]);
			startSong(songs[1]);
		}
	}
	
	public void defaultValues() {
		counter = 0;
		numSongs = 0;
	}
	
	public void stopSong(int song) {
		if(song > -1) {
			leech.stats.stopSong(song);
			leech.getNetworking().stopMp3(song);
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
