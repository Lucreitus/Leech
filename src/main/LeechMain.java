package main;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.Packet;


import com.maxmind.geoip.LookupService;

import network.Networking;
import network.PacketCap;
import network.TorrentControl;
import nodes.LocalHostNode;
import nodes.Node;
import nodes.TestLeechNode;
import nodes.TestSeedNode;
import nodes.TestTrackerNode;

import map.Convert;
import map.IPInfo;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import utils.FileCopy;
import visuals.Ocean;
import visuals.Stats;
import visuals.SystemMessage;
import vitamin.RenderTarget2D;
import vitamin.SurfaceFormat;
import vitamin.VGL;
import vitamin.VTexture2D;
import vitamin.VTimer;
import processing.video.*;


public class LeechMain extends PApplet{
		
	/** Version number.
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/** The width of the video. May not be the same size as the screen's physical width.
	 * 
	 */
	private int wth = (int)(screen.width);
	
	/**The height of the video. May not be same size as the screen physical height.
	 * 
	 */
	private int ht = (int)(screen.height);
	
	private IPInfo ipInfo;
	
	private PImage map,map2;
	private PShape vectorWorld;
	private float sizex,sizey = 0.0f;
	private double tint,tint2 = 0;
	float[] latlon;
	private PacketCap packetCap;
	public PFont font;
	private static ConcurrentHashMap<String,Node> nodes = new ConcurrentHashMap<String,Node>();
	public LookupService cl;
	private static JpcapCaptor captor;
	public static byte[] gwmac;
	public float x = 0;
	public float y = 0; 
	private TorrentControl torrentControl;
	private static Ocean ocean;
	private static Networking networking;
	static Stats stats;
	private static SystemMessage systemMessage;
	private String torrentName = "torrent";
	private boolean testing = false;
	private ConcurrentHashMap<String,Float> torrentFiles = new ConcurrentHashMap<String,Float>();
	private String downloadDirectory;
	
	//Vitamin
	VTimer timer;
	public VGL vgl;
	int _blurID;
	float rand;
	RenderTarget2D _offScreenRT;
	RenderTarget2D _gaussianHRT, _gaussianVRT, _gaussianRT;    // 2-pass gaussian blur filter
	float aspectRatio = WIDTH/(float)HEIGHT; 

	VTexture2D _vitaminTex;

	private boolean bExportVideo = false;

	private MovieMaker mm;
	public static int FRAME_RATE = 30;
	public static int MAX_FRAME_NB = FRAME_RATE * 120;
	public Automaticity auto;
	
	/** The main method. Do not touch unless you need to for some reason.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present","--hide-stop", "main.LeechMain" });
	}

	/** The setup method like in processing.
	 * 
	 */
	public void setup() {
		Global.AUTO = true;
		frameRate(FRAME_RATE);
		if((screen.width == 1280) && (screen.height == 1024)) {
			wth = 1280;
			ht = 960; 
		}
		//wth = 1440;
		//ht = 950;
		wth = screen.width;
		ht = screen.height;
		
		size(wth+1,ht+1,OPENGL);
		//hint( ENABLE_OPENGL_2X_SMOOTH ); 
		hint( DISABLE_OPENGL_ERROR_REPORT ); 
		//Video Capture
		//if (bExportVideo) {
		//    mm = new MovieMaker(this, wth, ht, "movie.mov", 24,
		//	MovieMaker.JPEG, MovieMaker.BEST);
		//}
		
		//============================================
		//Vitamin
		//============================================
		vgl = new VGL( this );
		//vgl.setVSync( true );
		
		_offScreenRT = new RenderTarget2D( vgl.gl(), 512, 512, SurfaceFormat.RGBA );
		_gaussianHRT = new RenderTarget2D( vgl.gl(), 512/2, 512/2, SurfaceFormat.RGBA );
		_gaussianVRT = new RenderTarget2D( vgl.gl(), 512/2, 512/2, SurfaceFormat.RGBA );
		_gaussianRT = new RenderTarget2D( vgl.gl(),(int)Global.WIDTH, (int)Global.HEIGHT, SurfaceFormat.RGBA );
		
		timer = new VTimer();
		timer.start();
		
		_blurID = vgl.addEffectFromFile( dataPath("blur.cgfx") );
		vgl.gl().glLineWidth( 2 );
		aspectRatio = Global.WIDTH/Global.HEIGHT; 
		//============================================
		//if(!testing) {
			//setupJpCap();
		//}
		
		x = Convert.lon(1);
		x = 720;
		y = Convert.lat(51);
		try {
			cl = new LookupService("/usr/local/share/GeoIP/GeoLiteCity.dat",LookupService.GEOIP_MEMORY_CACHE );
		} catch (IOException e1) {
			e1.printStackTrace(System.out);
		}		
		map = loadImage("vectorworldtextureOutline3.png");
		map.filter(INVERT);
		float ratio = screen.width / map.width;
		Global.LONG_ZERO = screen.width*0.5f;
		Global.WIDTH = screen.width;
		Global.HEIGHT = screen.height;
		sizex = map.width*ratio;
		sizey = map.height*ratio;
		nodes.put("localHost", new LocalHostNode("127.0.0.1",this));
		if(testing) {
			for(int i=0;i<50;i++) {
				nodes.put("testleech"+i, new TestLeechNode("127.0.0.1",this));
			}
			for(int i=0;i<8;i++) {
				nodes.put("testseed"+i, new TestSeedNode("127.0.0.1",this));
			}
			for(int i=0;i<4;i++) {
				nodes.put("Tracker"+i, new TestTrackerNode("127.0.0.1",this,"Tracker"+i));
			}
		}
		font = createFont("EurostileBold",20);
		textFont(font);
		networking = new Networking(this);
		packetCap = new PacketCap(captor);
		torrentControl = new TorrentControl(this);
		stats = new Stats(this,-30,ht-650);
		systemMessage = new SystemMessage(this,-30,ht-650);
		torrentControl.setDownloadLimit(100);
		torrentControl.setPeerLimit(30);
		auto = new Automaticity(this);
	}
	
	void enableVSync() {
		//PGraphicsOpenGL pgl = (PGraphicsOpenGL)g;
		//GL gl = pgl.beginGL( );
		//gl.setSwapInterval( 1 ); // use value 0 to disable v-sync
		//pgl.endGL( );
	}
	
	public void draw() {
		 // Stop on the wanted frame number
		/*
		  if (frameCount > MAX_FRAME_NB)
		  {
		    if (bExportVideo)
		    {
			mm.finish();
			delay(1000);
		    }
		    exit();
		  }
		*/
		background(0);
		//ocean.display();
		
		
		//systemMessage.draw();
		tint(25,215+random(20),235+random(20),200);
		image(map,-42,-30,map.width*0.6666f,map.height*0.6666f);
		
		Iterator<Node> iter = nodes.values().iterator();
		while(iter.hasNext()) {
			Node node = iter.next();
			//node.drawInitiated();
		}
		stats.draw();
		
		//UNCOMMENT TO GET THE FUCKING GLOW BACK :) :) :)
		glow();

	}
	
	private void test() {
		//===============================
		//TESTING
		//===============================
		if(testing) {
			if(random(10)>8.0) {
				for(int i=0;i<random(50)+1;i++) {
					try{
						if(random(1)<0.5) {
							int index = (int) random(nodes.size()-1);
							Node node = ((Node)nodes.values().toArray()[index]);
							node.sentPacket();
							LeechMain.getNetworking().sentPacket(
									LeechMain.getNodes().get("localHost").getRateUpload(),
									node.getLatitude(), 
									node.getLongitude(), 
									node.getProgress(), 
									LeechMain.getNodes().get("localHost").getProgress()
							);
						}
						if(random(1)<0.5) {
							int index = (int) random(nodes.size()-1);
							Node node = ((Node)nodes.values().toArray()[index]);
							node.receivedPacket();
							LeechMain.getNetworking().receivedPacket(
									LeechMain.getNodes().get("localHost").getRateUpload(),
									node.getLatitude(), 
									node.getLongitude(), 
									node.getProgress(), 
									LeechMain.getNodes().get("localHost").getProgress()
							);
						}
					} catch(Exception e) {
						e.printStackTrace(System.out);
					}
				}
			}
		}
	}
	
	private void glow() {
		//================================
		//Vitamin
		//================================
		 float time = timer.getCurrTime();
		    timer.update();
		    rand = random(0.33f, 0.44f);
		  
		    // direct access to jogl
		    vgl.begin();
		    
		    // no texture for lines
		    vgl.enableTexture( false );

		    // render to texture
		    _offScreenRT.bind();
		    renderScene( time, true );
		    _offScreenRT.unbind();
		    
		    // apply a gaussian blur filter
		    applyGaussianFilter( time );
		    
		    // Render to main framebuffer  
		    renderScene( time, false );

		    // render fullscreen aligned quad with blurred scene texture
		    vgl.ortho();
		    vgl.setAdditiveBlend();
		    vgl.setDepthMask( false );
		    _gaussianRT.getTexture().enable();
		    vgl.fill( 1, rand );
		    vgl.rect( 1, -1 );
		    vgl.rect( 1, -1 );    // render twice for a better glow
		    vgl.rect( 1, -1 );    // render twice for a better glow
		    //vgl.rect( 1, -1 );
		    _gaussianRT.getTexture().disable();
		    // restore modes
		    vgl.setAlphaBlend();
		    vgl.setDepthMask( true );

		    // end direct rendering
		    vgl.end();
	}
	
	public void mouseDragged() {

	}

	public void mousePressed() {

	}
	
	public void mouseReleased() {
		
	}
	
	public void keyPressed() {
		if(key == 'r') {
			this.restart();
			auto.defaultValues();
		};
		if(key == 's') {
			torrentControl.startTorrent();
		};
		if(key == 't') {
			torrentControl.stopTorrent();
		};
		if(key == 'd') {
			torrentControl.setDownloadLimit(torrentControl.getDownloadLimit()+5);
		};
		if(key == 'l') {

			stats.playSong();
			Iterator<Entry<String,Float>> iter = getTorrentFiles().entrySet().iterator();
			String name = "nothing";
			boolean complete = false;
			int i=0;
			while(iter.hasNext()) {
				Entry<String,Float> entry = iter.next();
				if(entry.getKey().contains(".mp3")) {
					if(i == stats.getSelection()) {
						name = entry.getKey();
						if(entry.getValue()>=1) {
							complete = true;
						}
					}
					i++;
				}
			}
			
			if(complete) {
				networking.sendLFMp3(nodes.get("localHost").getRateUpload(),nodes.get("localHost").getRateDownload(),getNodes().get("localHost").getProgress(),name,stats.getSelection());
			} else {
				String subName = name.split("/")[1];
				try {
					FileCopy.copy(
						downloadDirectory+"/"+name+".part",
						downloadDirectory+"/unfinished/"+subName
					);
					networking.sendLFMp3(nodes.get("localHost").getRateUpload(),nodes.get("localHost").getRateDownload(),getNodes().get("localHost").getProgress(),"/unfinished/"+subName,stats.getSelection());
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
			}
		
		};
		
		if(key == 'p') {

			stats.playSong();
			Iterator<Entry<String,Float>> iter = getTorrentFiles().entrySet().iterator();
			String name = "nothing";
			boolean complete = false;
			int i=0;
			while(iter.hasNext()) {
				Entry<String,Float> entry = iter.next();
				if(entry.getKey().contains(".mp3")) {
					if(i == stats.getSelection()) {
						name = entry.getKey();
						if(entry.getValue()>=1) {
							complete = true;
						}
					}
					i++;
				}
			}
			
			//if(complete) {
				//networking.sendMp3(nodes.get("localHost").getRateUpload(),nodes.get("localHost").getRateDownload(),getNodes().get("localHost").getProgress(),"/unfinished/wavs/"+subName,stats.getSelection());
			//} else {
				String subName = name.split("/")[1];
				networking.sendMp3(nodes.get("localHost").getRateUpload(),nodes.get("localHost").getRateDownload(),getNodes().get("localHost").getProgress(),"/unfinished/wavs/"+subName,stats.getSelection());
			//}
		
		};
		if(key == 'o') {
			stats.stopSong();
			networking.stopMp3(stats.getSelection());		
		};
		if(key == DOWN) {
			stats.moveDown();
		};
		if(key == UP) {
			stats.moveUp();
		};
		if(key == '[') {
			stats.moveDown();
		};
		if(key == ']') {
			stats.moveUp();
		};
	}
	
	void renderScene( float time, boolean doBlur )
	{
	  vgl.background( 0 );	  
	  
	  if(wth==1024&&ht==768) {
		  vgl.ortho( -20, Global.WIDTH, 20, Global.HEIGHT, -10, 10 );
		  
		  vgl.perspective(-45, aspectRatio, 1, 100000 );
		  vgl.camera(0,0, -2000, 0, 0, 0, 0, 1, 0 );
		  vgl.translate(-270, -90);
	  } else {
		  vgl.ortho( -20, Global.WIDTH, 20, Global.HEIGHT, -10, 10 );
		  vgl.perspective(-45, aspectRatio, 1, 100000 );
		  vgl.camera(0,0, -2000, 0, 0, 0, 0, 1, 0 );
	  }
	  

	  //vgl.rotateY((mouseY/Global.HEIGHT)*360);
	  //vgl.rotateX((mouseY/Global.WIDTH)*360);
	  vgl.rotateY(0);
	  vgl.rotateX(0);
	  if(wth==1400 && ht==900) {
		  vgl.translate(0,-70);
	  }

	  stats.draw();
	  tint(25,215+random(20),235+random(20),240);
	  image(map,-42,-30,map.width*0.6666f,map.height*0.6666f);
	  if( doBlur ) vgl.fill( 0, 1, 1, rand );
	  else vgl.fill( 1, 0.9f, 0.8f, rand );
	    
	   //vgl.line( -100, 100, 0, 0, -150, 0 );
	   //vgl.line( 0, -150, 0, 100, 100, 0 );
	   //vgl.line( -100, 100, 0, 100, 100, 0 );
	  	if(wth==1024&&ht==768) {
	  		vgl.translate(210, 0);
	  	} else if(wth==1400 && ht==900) {
	  		vgl.translate(30,0);
	  	}
	    Iterator<Node> iter = nodes.values().iterator();
		while(iter.hasNext()) {
			Node node = iter.next();
			node.drawInitiated();
		}

	    // cancel rotation done above.. it would work with a push/pop matrix aswell
	    //vgl.rotateX( -sin(time*2)*10 );
	    //vgl.rotateY( -(time)*30 );
	  
	    //vgl.translate( 0, 0, mouseY-500 );  
	    //_vitaminTex.enable();
	    //vgl.rect( 250, -250*(HEIGHT/(float)WIDTH) );
	    //_vitaminTex.disable();
	}  
	
	void applyGaussianFilter( float time ) { 
	    // First pass.. vertical sampling
	    _gaussianVRT.bind();
	    vgl.background( 0 );
	    vgl.ortho();     
	    vgl.setShader( _blurID );   
	    vgl.setParameter2f( "texSize", _gaussianVRT.getWidth(), _gaussianVRT.getHeight() );     
	    vgl.setTextureParameter( "ScnMap", _offScreenRT.getTexture().getID() ); 
	    vgl.getActiveEffect().setFirstPass( "Technique_GuassianBlurV" );
	    vgl.getActiveEffect().setPass(); 
	    vgl.rect( 1, 1 );
	    vgl.getActiveEffect().resetPass();
	    vgl.disableShader();   
	    _gaussianVRT.unbind();


	    // Second pass.. horizontal sampling
	    _gaussianHRT.bind();
	    vgl.background( 0 );
	    vgl.ortho();     
	    vgl.setShader( _blurID );   
	    vgl.setParameter2f( "texSize", _gaussianHRT.getWidth(), _gaussianHRT.getHeight() );     
	    vgl.setTextureParameter( "ScnMap", _gaussianVRT.getTexture().getID() ); 
	    vgl.getActiveEffect().setFirstPass( "Technique_GuassianBlurH" );
	    vgl.getActiveEffect().setPass(); 
	    vgl.rect( 1, 1 );
	    vgl.getActiveEffect().resetPass();
	    vgl.disableShader();   
	    _gaussianHRT.unbind();
	    
	    
	    // Upscale blurred texture
	    _gaussianRT.bind();
	    vgl.background( 0 );
	    vgl.ortho();     
	    vgl.setShader( _blurID );   
	    vgl.setTextureParameter( "ScnMap", _gaussianHRT.getTexture().getID() ); 
	    vgl.getActiveEffect().setFirstPass( "Technique_Copy" );
	    vgl.getActiveEffect().setPass(); 
	    vgl.rect( 1, 1 );
	    vgl.getActiveEffect().resetPass();
	    vgl.disableShader();   
	    _gaussianRT.unbind();
	}

	
	
	
	public void stop() {
	  vgl.release();    // destroy up vgl
	  
	  super.stop();
	}
	
	public static ConcurrentHashMap<String,Node> getNodes() {
		return nodes;
	}
	public static JpcapCaptor getCaptor() {
		return captor;
	}
	
	
	private void setupJpCap() {
		//initialize Jpcap
		for(int i=0;i<JpcapCaptor.getDeviceList().length;i++) {
			System.out.println(JpcapCaptor.getDeviceList()[i].name);
		}
		
		NetworkInterface device = JpcapCaptor.getDeviceList()[3];
		try {
			captor = JpcapCaptor.openDevice(device,2000,false,5000);
		
			for(NetworkInterfaceAddress addr:device.addresses) {
				if(addr.address instanceof Inet4Address){
				//thisIP=addr.address;
					break;
				}
			}
			
			//obtain MAC address of the default gateway
			InetAddress pingAddr;
			pingAddr = InetAddress.getByName("www.google.co.uk");
	
			/*
			//captor.setFilter("tcp and dst host "+pingAddr.getHostAddress(),true);
			gwmac=null;
			while(true){
				new URL("http://www.google.co.uk").openStream().close();
				Packet ping=captor.getPacket();
				if(ping==null){
					System.out.println("cannot obtain MAC address of default gateway.");
					System.exit(-1);
				}else if(Arrays.equals(((EthernetPacket)ping.datalink).dst_mac,device.mac_address))
					continue;
				gwmac=((EthernetPacket)ping.datalink).dst_mac;
				break;
			}
			*/
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		
	}
	
	
	public static void processPacket(Packet packet) {
		//System.out.println(packet);
		String ip[] = packet.toString().split("/");
		//System.out.println(packet.sec);
		//System.out.println(packet.datalink.toString());
		//System.out.println(packet.header);
		//System.out.println(packet.data);
		//System.out.println(packet.EOF.toString());
		try{
			ip[2] = ip[2].split(" ")[0];
			//System.out.println(ip[2]);
			nodes.get(ip[2]).sentPacket();
			networking.sentPacket(nodes.get("localHost").getRateUpload(), nodes.get(ip[2]).getLatitude(), nodes.get(ip[2]).getLongitude(), nodes.get(ip[2]).getProgress(), nodes.get("localHost").getProgress());
		} catch(Exception e) {
			
		}
		
		try{
			ip[1] = ip[1].substring(0, ip[1].length()-2);
			//System.out.println(ip[1]);
			nodes.get(ip[1]).receivedPacket();
			networking.receivedPacket(nodes.get("localHost").getRateDownload(), nodes.get(ip[1]).getLatitude(), nodes.get(ip[1]).getLongitude(), nodes.get(ip[1]).getProgress(), nodes.get("localHost").getProgress());
		} catch(Exception e) {
			
		}		
	}
	
	
	private void playSong() {
		stats.playSong();
		Iterator<Entry<String,Float>> iter = getTorrentFiles().entrySet().iterator();
		String name = "nothing";
		boolean complete = false;
		int i=0;
		while(iter.hasNext()) {
			Entry<String,Float> entry = iter.next();
			if(entry.getKey().contains(".mp3")) {
				if(i == stats.getSelection()) {
					name = entry.getKey();
					if(entry.getValue()>=1) {
						complete = true;
					}
				}
				i++;
			}
		}
		
		if(complete) {
			networking.sendMp3(nodes.get("localHost").getRateUpload(),nodes.get("localHost").getRateDownload(),getNodes().get("localHost").getProgress(),name,stats.getSelection());
		} else {
			String subName = name.split("/")[1];
			try {
				FileCopy.copy(
					downloadDirectory+"/"+name+".part",
					downloadDirectory+"/unfinished/"+subName
				);
				networking.sendMp3(nodes.get("localHost").getRateUpload(),nodes.get("localHost").getRateDownload(),getNodes().get("localHost").getProgress(),"/unfinished/"+subName,stats.getSelection());
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
		}
	}
	
	public static Ocean getOcean() {
		return ocean;
	}
	
	public float getWth() {
		return wth;
	}
	
	public float getHt() {
		return ht;
	}
	public void setTorrentName(String torrentName) {
		this.torrentName = torrentName;
	}
	public String getTorrentName() {
		return torrentName;
	}
	public ConcurrentHashMap<String,Float> getTorrentFiles() {
		return torrentFiles;
	}
	
	public static Networking getNetworking() {
		return networking;
	}
	public void setDownloadDirectory(String downloadDirectory) {
		this.downloadDirectory = downloadDirectory;
		networking.sendDownloadDirectory(downloadDirectory);
	}
	public String getDownloadDirectory() {
		return downloadDirectory;
	}
	
	public void restart() {
		networking.resetSC();
		torrentControl.deleteData();
		nodes.clear();
		stats.clear();
		nodes.put("localHost", new LocalHostNode("127.0.0.1",this));
		torrentControl.resetTorrent();
		torrentControl.resetTorrent();
		torrentControl.resetTorrent();
		torrentControl.resetTorrent();
		torrentControl.setDownloadLimit(100);
		torrentControl.setPeerLimit(30);
		torrentControl.setDownloadLimit(100);
		torrentControl.setPeerLimit(30);
	}
	
	
}
