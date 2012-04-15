package visuals;

import processing.core.PApplet;
import main.LeechMain;

public class Ocean {
	LeechMain leech;
	int num = 1000;
	float[] buf1,buf2;
	float[] colors;
	float a = 0;
	float[][] sizes;
	float[][] sizeRs;
	float[][] sizeRates;
	
	public Ocean(LeechMain leech) {
		this.leech = leech;
		buf1 = new float[256];
		buf2 = new float[256];
		sizes = new float[(int)((leech.getWth()*0.05))][(int)((leech.getHt()*0.05))];
		sizeRs = new float[(int)((leech.getWth()*0.05))][(int)((leech.getHt()*0.05))];
		sizeRates = new float[(int)((leech.getWth()*0.05))][(int)((leech.getHt()*0.05))];
		
		for(int x=0;x<leech.getWth()*0.05;x++) {
			for(int y=0;y<leech.getHt()*0.05;y++) {
				sizeRates[x][y] = leech.random(0.035f)+0.015f;
				sizeRs[x][y] = leech.random(0,PApplet.PI);
			}
		}
	}
	
	public void display() {		
		for(int x=0;x<leech.getWth()*0.05;x++) {
			for(int y=0;y<leech.getHt()*0.05;y++) {
				sizeRs[x][y] = (sizeRs[x][y]+sizeRates[x][y])%(PApplet.PI*2);
				sizes[x][y] = (float)Math.sin(sizeRs[x][y])*30+37;
				leech.stroke(0,150,255,175);
				leech.noFill();
				leech.ellipse(x*20, y*20, sizes[x][y], sizes[x][y]);
			}
		}
		
		
		/*
		for(int i=0;i<256;i++) {
			leech.pushMatrix();
			leech.noFill();
			//leech.strokeWeight(1);
			//leech.stroke(0,75,100,255);
			leech.fill(255,255,255,255*buf1[i]*2);
			float x = ((leech.width/128)*i)-800;
			float y = (leech.height*0.0f);
			//float z = (1000*buf1[i])-1000;
			leech.translate(x,y,-800);
			leech.ellipse(0, 0,3000*buf1[i],2000*buf1[i]);
			//leech.translate(x,y,z);
			//leech.rect(0, -10, 10, -1 * 1000*buf1[i]+50);
			leech.noStroke();
			//for(int i = 0;)
			leech.popMatrix();
		}
		
		for(int i=0;i<256;i++) {
			leech.pushMatrix();
			leech.noFill();
			//leech.strokeWeight(1);
			//leech.stroke(0,75,100,255);
			leech.fill(255,255,255,255*buf2[i]*2);
			float x = ((leech.width/128)*i)-800;
			float y = (leech.height);
			//float z = (1000*buf2[i])-1000;
			leech.translate(x,y,-800);
			leech.ellipse(0, 0,3000*buf2[i],2000*buf2[i]);
			//leech.translate(x,y,z);
			//leech.rect(0, -10, 10, -1 * 1000*buf1[i]+50);
			leech.noStroke();
			//for(int i = 0;)
			leech.popMatrix();
		}
		*/
		
		/*
		for(float i3=0;i3<6;i3++) {
			for(int i=0;i<256;i++) {
				leech.pushMatrix();
				leech.noFill();
				//leech.strokeWeight(1);
				//leech.stroke(0,75,100,255);
				leech.fill(255,255,255,255*buf1[i]);
				float x = ((leech.width/128)*i)-800;
				float y = (leech.height*(i3/6)*2);
				//float z = (1000*buf1[i])-1000;
				leech.translate(x,y,-800);
				leech.ellipse(0, 0,3000*buf1[i],2000*buf1[i]);
				//leech.translate(x,y,z);
				//leech.rect(0, -10, 10, -1 * 1000*buf1[i]+50);
				leech.noStroke();
				//for(int i = 0;)
				leech.popMatrix();
			}
		}
		*/
		
	}
	
	public void setBuf1(int i, float f) {
		buf1[i] = f;
		buf1[i+128] = f;
	}
	public void setBuf2(int i, float f) {
		buf2[i] = f;
		buf2[i+128] = f;
	}
}
