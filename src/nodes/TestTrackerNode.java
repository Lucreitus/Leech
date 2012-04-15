package nodes;

import main.LeechMain;

public class TestTrackerNode extends TrackerNode {

	public TestTrackerNode(String ip, LeechMain parent, String name) {
		super(ip, parent, name);
		setLatitude(parent.random(-40,70));
		setLongitude(parent.random(-100,100));
		initiated = true;
	}

}
