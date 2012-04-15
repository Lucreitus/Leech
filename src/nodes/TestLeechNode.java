package nodes;

import main.LeechMain;

public class TestLeechNode extends LeechNode{

	public TestLeechNode(String ip, LeechMain parent) {
		super(ip, parent);
		setLatitude(parent.random(-40,70));
		setLongitude(parent.random(-100,100));
		initiated = true;
	}

}
