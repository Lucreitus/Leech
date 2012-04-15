package nodes;

import network.TraceRoute;
import main.LeechMain;
import map.Convert;

public class TrackerNode extends Node {

	public TrackerNode(String ip, LeechMain parent, String name) {
		super(ip, parent);
		if(ip.equals("95.211.88.49")) {
			//new TraceRoute(this,parent);
		}
		this.name = name;
		color[0] = 0;
		color[1] = 100;
		color[2] = 255;
		color[3] = 255;
		receivePacketTime = 400;
	}
	
	public void draw() {
		parent.stroke(color[0],color[1],color[2],color[3]);
		parent.noFill();
		parent.ellipse(x,y,size,size);
		parent.ellipse(x,y,size2,size2);
		parent.strokeWeight(2.5f);
		//parent.ellipse(x,y,size3,size3);
		//parent.textFont(parent.font);
		//parent.fill(0,0,0,180);
		//parent.rect(x-5, y+10, 140, 25);
		//parent.fill(255);
		//parent.text(name,x,y+20);
		//parent.text(ip,x,y+30);
		//parent.text(location,x,y+30);
		drawRelayConnections();
	}
	
	public void drawRelayConnections() {
		parent.stroke(255);
		if(traceRoute.size()>=1) {
			parent.line(parent.x,parent.y,traceRoute.get(0).x, traceRoute.get(0).y);
		}
		
		for(int i=0;i<traceRoute.size() -1;i++) {
			parent.fill(255);
			parent.line(traceRoute.get(i).x, traceRoute.get(i).y, traceRoute.get(i+1).x, traceRoute.get(i+1).y);
		}
		parent.stroke(0);
	}
}
