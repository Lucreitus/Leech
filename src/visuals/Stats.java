package visuals;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.lang.model.element.Element;

import main.LeechMain;

public class Stats {
	LeechMain leech;
	private float x;
	private float y;
	private int selection = 0;
	private boolean[] playingSongs;
	//private float offset = 400;
	
	public Stats(LeechMain leech, float x, float y) {
		this.leech = leech;
		this.x = x;
		this.y = y;
	}
	
	public void draw() {
		if(playingSongs == null) {
			if(leech.getTorrentFiles().size() > 0 ) {
				playingSongs = new boolean[leech.getTorrentFiles().size()];
				for(int i=0;i<playingSongs.length;i++) {
					playingSongs[i] = false;
				}
			}
		}
		
		leech.fill(0,0,0,180);
		leech.stroke(25,215+leech.random(20),235+leech.random(20),200+leech.random(50));
		//leech.rect(x-5, y-10, 320, 340);
		leech.fill(255);
		leech.text("Progress: "+ (leech.getNodes().get("localHost").getProgress()*100),x,y-5);
		if(leech.getNodes().get("localHost").getProgress()==1) {
			leech.fill(25,235+leech.random(20),25+leech.random(20),200+leech.random(50));
		} else {
			leech.fill(25,215+leech.random(20),235+leech.random(20),200+leech.random(50));
		}
		leech.rect(x, y+10, 750*(leech.getNodes().get("localHost").getProgress()), 20);
		leech.fill(255);
		leech.text("Number of Seeders: "+leech.getNodes().size(),x,y+50);
		leech.text("Download Rate: "+ leech.getNodes().get("localHost").getRateDownload(),x,y+75);
		leech.text("Upload Rate: "+ leech.getNodes().get("localHost").getRateUpload(),x,y+100);
		leech.text("Torrent Name: " + leech.getName(),x,y+125);
		leech.text("Files: ",x,y+150);
		Iterator<Entry<String,Float>> iter = leech.getTorrentFiles().entrySet().iterator();
		int i=0;
		while(iter.hasNext()) {
			Entry<String,Float> entry = iter.next();
			if(entry.getKey().contains(".mp3")) {
				if(entry.getValue()==1) {
					leech.fill(25,235+leech.random(20),25+leech.random(20),200+leech.random(50));
					leech.rect(x, y+155f+(i*25)+6, 750*(entry.getValue()), 12f);
				} else {
					leech.fill(25,215+leech.random(20),235+leech.random(20),200+leech.random(50));
					leech.rect(x, y+155f+(i*25)+6, 750*(entry.getValue()), 12f);
				}
				leech.fill(255);
				if(playingSongs != null) {
					if(playingSongs[i]) {
						//leech.strokeWeight(4);
						leech.fill(205+leech.random(20),0,0,220+leech.random(10));
						leech.rect(x, y+155f+(i*25)+6, 750*(entry.getValue()), 12f);
					}
					leech.noStroke();
				}
				leech.text(entry.getKey(),x,y+170+(i*25));
				i++;
			}
		}
		
		leech.noStroke();
		leech.noFill();
		
		//Select Box
		leech.stroke(235+leech.random(20),25+leech.random(20),25+leech.random(20),150+leech.random(10));
		leech.rect(x, y+155f+(selection*25), 750, 15f);
	}
	
	public void clear() {
		for(int i=0;i<playingSongs.length;i++) {
			playingSongs[i] = false;
		}
	}
	
	public void playSong(int song) {
		playingSongs[song] = true;
	}
	
	public void stopSong(int song) {
		playingSongs[song] = false;
	}
	
	public void moveDown() {
		selection = (selection -1);
		if(selection < 0 ) selection = 10;
	}
	
	public void moveUp() {
		selection = (selection +1) %11;
	}
	
	public int getSelection() {
		return selection;
	}
	
	public void playSong() {
		playingSongs[selection] = true;
	}
	
	public void stopSong() {
		playingSongs[selection] = false;
	}
}
