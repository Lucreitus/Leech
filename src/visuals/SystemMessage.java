package visuals;

import main.LeechMain;

public class SystemMessage {
	
		LeechMain leech;
		private float x;
		private float y;
		int timer = 10000;
		private String message = "Test Message";
		
		
		public SystemMessage(LeechMain leech, float x, float y) {
			this.leech = leech;
			this.x = x;
			this.y = y;
		}
		
		public void draw() {
			//if(timer >0) {
				leech.stroke(25,215+leech.random(20),235+leech.random(20),200+leech.random(50));
				leech.fill(x,y,200,150+leech.random(20));
				leech.rect(x,y,leech.getWth() - 200,leech.getHt() - 200);
				leech.rect(x,y,300,300);
				System.out.println("test");
			//}
			//timer = timer-1;
		}
}
