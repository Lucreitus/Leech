package nodes;

import network.TraceRoute;
import main.LeechMain;

public class LeechNode extends Node{

	public LeechNode(String ip, LeechMain parent) {
		super(ip, parent);
		//new TraceRoute(this,parent);
		color[0] = 255;
		color[1] = 255;
		color[2] = 255;
		color[3] = 255;
	}

	public void draw() {
		if(progress>0.999) {
			parent.stroke(25,255,75,255);
		} else {
			parent.stroke(color[0],color[1],color[2],color[3]);
		}
		parent.noFill();
		parent.ellipse(x,y,size,size);
		parent.ellipse(x,y,size2,size2);
		//parent.ellipse(x,y,size3,size3);
	}
}
