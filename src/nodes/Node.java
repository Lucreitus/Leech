package nodes;

import java.util.ArrayList;

import processing.core.PApplet;
import vitamin.VGL;

import main.LeechMain;
import map.Convert;
import map.IPInfo;

public class Node {
	String name;
	float longitude = 0.0f;
	float latitude = 0.0f;
	String ip = "127.0.0.1";
	String location = "none";
	LeechMain parent;
	float x,y,rateUpload,rateDownload=0;
	float progress = 0.0f;
	ArrayList<RelayNode> traceRoute = new ArrayList<RelayNode>();
	boolean initiated = false;
	float packetCounter,packetCounter2 = 0;
	float receivePacketTime = 4;
	float sendPacketTime = 4;
	String tracker = "none";
	float sizeR=0;
	float size=10;
	float sizeRate;
	float sizeR2=0;
	float size2=10;
	float sizeRate2;
	float sizeR3=0;
	float size3=10;
	float sizeRate3;
	float[] color;
	float[] packetColor;
	byte[] pieces;
	VGL vgl;
	
	public Node(String ip, LeechMain parent) {
		this.ip = ip;
		this.parent = parent;
		this.x = parent.x;
		this.y = parent.y;
		color = new float[4];
		packetColor = new float[4];
		packetColor[0] = 100;
		packetColor[1] = 150+parent.random(100);
		packetColor[2] = 200+parent.random(50);
		packetColor[3] = 255;
		new IPInfo(this, parent);
		sizeRate = parent.random(0.035f)+0.015f;
		sizeRate2 = parent.random(0.035f)+0.015f;
		initiated = true;
		this.vgl = parent.vgl;
	}
	
	public void drawInitiated() {
		if(initiated) {
			sizeR = (sizeR+sizeRate)%(PApplet.PI*2);
			size = (float)Math.sin(sizeR)*10+13;
			sizeR2 = (sizeR2+sizeRate2)%(PApplet.PI*2);
			size2 = (float)Math.sin(sizeR2)*8+11;
			sizeR3 = (sizeR3+sizeRate3)%(PApplet.PI*2);
			size3 = (float)Math.sin(sizeR3)*8+11;
			draw();
			for(int i=0;i<traceRoute.size();i++) {
				traceRoute.get(i).drawInitiated();
			}
			if(packetCounter>0) {
				drawReceivePacketEllipse();
				drawPacket();
			}
			packetCounter = Math.max(0, packetCounter -1);
			if(packetCounter2>0) {
				drawSendPacketEllipse();
				drawSendPacket();
			}
			packetCounter2 = Math.max(0, packetCounter2 -1);
		}
	}
	
	public void draw() {
		parent.fill(255);
		parent.ellipse(x,y,10,10);
		parent.textFont(parent.font);
		parent.fill(0,0,0,180);
		parent.rect(x-5, y+10, 70, 40);
		parent.fill(255);
		parent.text(name,x,y+20);
		parent.text(location,x,y+30);
		parent.text(ip,x,y+40);		
	}
	
	public void drawPacket() {
		parent.strokeWeight(2.5f);
		if(progress>0.999) {
			receivePacketTime = 6;
			sendPacketTime = 6;
			parent.stroke(25,255,75,100*(packetCounter/receivePacketTime));
		} else {
			parent.stroke(color[0],color[1],color[2],100*(packetCounter/receivePacketTime));
		}
		parent.noFill();
		parent.curve(
			x, 
			y+400, 
			x, 
			y, 
			parent.x, 
			parent.y, 
			parent.x, 
			parent.y+400
		);
		parent.noStroke();
		parent.strokeWeight(1);
	}
	
	public void drawSendPacket() {
		parent.strokeWeight(2.5f);
		parent.stroke(0,parent.random(200,255),parent.random(200,255),100*(packetCounter2/sendPacketTime));

		parent.noFill();
		parent.curve(
				x, 
				y-200, 
				x, 
				y, 
				parent.x, 
				parent.y, 
				parent.x, 
				parent.y-200
			);
		parent.noStroke();
		parent.strokeWeight(1);
	}
	
	public void drawReceivePacketEllipse() {
		if(progress>0.999) {
			parent.fill(25,255,75,100*(packetCounter/receivePacketTime));
		} else {
			parent.fill(color[0],color[1],color[2],100*(packetCounter/receivePacketTime));
		}
		//parent.stroke(0,0,0,255);
		parent.ellipse(x, y, size*2, size*2);
		parent.ellipse(x, y, size2+10, size2+10);
		parent.noStroke();
	}
	
	public void drawSendPacketEllipse() {
		parent.fill(parent.random(0,55),parent.random(150,255),parent.random(200,255),100*(packetCounter2/sendPacketTime));
		//parent.stroke(0,0,0,255);
		parent.ellipse(parent.x, parent.y, size*2, size*2);
		parent.ellipse(parent.x, parent.y, Math.abs(size2)+20,Math.abs(size2)+20);
		parent.noStroke();
	}
	
	//==========================================================
	//Vitamin
	//==========================================================
	public void vglDrawInitiated(boolean doBlur, float rand) {
		if(initiated) {
			sizeR = (sizeR+sizeRate)%(PApplet.PI*2);
			size = (float)Math.sin(sizeR)*10+13;
			sizeR2 = (sizeR2+sizeRate2)%(PApplet.PI*2);
			size2 = (float)Math.sin(sizeR2)*8+11;
			sizeR3 = (sizeR3+sizeRate3)%(PApplet.PI*2);
			size3 = (float)Math.sin(sizeR3)*8+11;
			draw();
			for(int i=0;i<traceRoute.size();i++) {
				//traceRoute.get(i).drawInitiated();
			}
			if(packetCounter>0) {
				//vglDrawReceivePacketEllipse();
				vglDrawPacket(doBlur,rand);
			}
			packetCounter = Math.max(0, packetCounter -1);
			if(packetCounter2>0) {
				//vglDrawSendPacketEllipse();
				vglDrawSendPacket(doBlur,rand);
			}
			packetCounter2 = Math.max(0, packetCounter2 -1);
		}
	}
	
	public void vglDrawPacket(boolean doBlur, float rand) {
		if(progress>0.999) {
			receivePacketTime = 6;
			sendPacketTime = 6;
			if(doBlur) {
				vgl.fill( 0.1f, 1, 0.25f, 0.7f*(packetCounter/receivePacketTime));
			} else {
				vgl.fill( 1, 0.9f, 0.8f, 0.7f*(packetCounter/receivePacketTime) );
			}
		} else {
			if(doBlur) {
				vgl.fill( color[0]/255, color[1]/255, color[2]/255, 0.7f*(packetCounter/receivePacketTime));
			} else {
				vgl.fill( 1, 0.9f, 0.8f, 0.7f*(packetCounter/receivePacketTime) );
			}
		}
		vgl.line(x,y,0,parent.x,parent.y,0);
	}
	
	public void vglDrawSendPacket(boolean doBlur,float rand) {
		//parent.stroke(0,parent.random(200,255),parent.random(200,255),150*(packetCounter2/sendPacketTime));
		if(doBlur) {
			vgl.fill( 0,parent.random(0.8f,1),parent.random(0.8f,1), 0.7f*(packetCounter/receivePacketTime));
		} else {
			vgl.fill( 1, 0.9f, 0.8f, 0.7f*(packetCounter/receivePacketTime) );
		}
		vgl.line(x,y,0,parent.x,parent.y,0);
	}
	
	public void setLatitude(float latitude) {
		this.latitude = latitude;
		y = Convert.lat(latitude);
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
		x = Convert.lon(longitude);
	}
	public void setInitiated(boolean initiated) {
		this.initiated = initiated;
	}
	public String getIP() {
		return ip;
	}
	public void addRelayNode(RelayNode relayNode) {
		this.traceRoute.add(relayNode);
		System.out.println("traceRoute size = " + traceRoute.size());
	}
	public void receivedPacket() {
		this.packetCounter = receivePacketTime;
	}
	public void sentPacket() {
		this.packetCounter2 = sendPacketTime;
	}
	
	public float getRateDownload() {
		return rateDownload;
	}
	public float getRateUpload() {
		return rateUpload;
	}
	public void setRateUpload(float rateUpload) {
		this.rateUpload = rateUpload*0.001f;
	}
	public void setRateDownload(float rateDownload) {
		this.rateDownload = rateDownload*0.001f;
	}
	public void setProgress(float progress) {
		this.progress = progress;
	}
	public float getProgress() {
		return progress;
	}
	public void setPieces(byte[] pieces) {
		this.pieces = pieces;
	}
	public byte[] getPieces() {
		return pieces;
	}
	public float getLatitude() {
		return latitude;
	}
	public float getLongitude() {
		return longitude;
	}
}
