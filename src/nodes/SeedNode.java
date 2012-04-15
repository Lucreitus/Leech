package nodes;

import main.LeechMain;

public class SeedNode extends Node{
	
	public SeedNode(String ip, LeechMain parent) {
		super(ip, parent);
		progress = 100.0f;
		color[0] = 20;
		color[1] = 255;
		color[2] = 20;
		color[3] = 255;
	}

	public void draw() {
		parent.stroke(color[0],color[1],color[2],color[3]);
		parent.noFill();
		parent.ellipse(x,y,size,size);
		parent.ellipse(x,y,size2,size2);
		//parent.ellipse(x,y,size3,size3);
	}
}
