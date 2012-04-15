package nodes;

import main.LeechMain;

public class LocalHostNode extends Node{
	
	public LocalHostNode(String ip, LeechMain parent) {
		super(ip, parent);
		setLatitude(51.0f);
		setLongitude(0);
		initiated = true;
		color[0] = 255;
		color[1] = 0;
		color[2] = 0;
		color[3] = 255;
	}
	
	public void draw() {
		parent.stroke(color[0],color[1],color[2],color[3]);
		parent.noFill();
		parent.ellipse(x,y,size,size);
		parent.ellipse(x,y,size2,size2);
		//parent.ellipse(x,y,size3,size3);
		//parent.textFont(parent.font);
		//parent.fill(0,0,0,180);
		//parent.rect(x-5, y+10, 100, 25);
		//parent.fill(255);
		//parent.text("LocalHost",x,y+20);
		//parent.text(ip,x,y+30);
	}

}
