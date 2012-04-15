package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import main.LeechMain;

public class Networking {
	LeechMain leech2;
	int receivingPort = 5000;
	int sendingPort = 57120;
	OSCPortIn oscPortIn;
	OSCPortOut oscPortOut;
	SCUpdateThread scUpdateThread;
	
	public Networking(LeechMain leech) {
		this.leech2 = leech;
		try {
			oscPortIn = new OSCPortIn(receivingPort);
			InetAddress address = InetAddress.getLocalHost();
			oscPortOut = new OSCPortOut(address,sendingPort);
		} catch (SocketException e) {
			e.printStackTrace(System.out);
		} catch (UnknownHostException e) {
			e.printStackTrace(System.out);
		}
		scUpdateThread = new SCUpdateThread(leech, this);
		
		/*
		//AudioIn1
		oscPortIn.addListener("/audio0",new OSCListener() {
			//@Override
			public void acceptMessage(Date arg0, OSCMessage arg1) {
				for(int i=0;i<128;i++) {
					LeechMain.getOcean().setBuf1(i,((Number)arg1.getArguments()[i]).floatValue());
				}
			}
        });
		//AudioIn2
		oscPortIn.addListener("/audio1",new OSCListener() {
			//@Override
			public void acceptMessage(Date arg0, OSCMessage arg1) {
				for(int i=0;i<128;i++) {
					LeechMain.getOcean().setBuf2(i,((Number)arg1.getArguments()[i]).floatValue());
				}
			}
        });
        */
		//AudioIn1
		oscPortIn.addListener("/resendMp3s",new OSCListener() {
			//@Override
			public void acceptMessage(Date arg0, OSCMessage arg1) {
				System.out.println("Resending Mp3s");
				leech2.auto.resendSongs();
			}
		});
		oscPortIn.startListening();
	}
	
	public void receivedPacket(float downloadRate,float latitude, float longitude, float progress, float localHostProgress) {
		OSCMessage message = new OSCMessage("/receivedPacket");
		message.addArgument(downloadRate);
		message.addArgument(latitude);
		message.addArgument(longitude);
		message.addArgument(progress);
		message.addArgument(localHostProgress);
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	public void sentPacket(float uploadRate,float latitude, float longitude, float progress, float localHostProgress) {
		OSCMessage message = new OSCMessage("/sentPacket");
		message.addArgument(uploadRate);
		message.addArgument(latitude);
		message.addArgument(longitude);
		message.addArgument(progress);
		message.addArgument(localHostProgress);
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void sendMp3(float uploadRate,float downloadRate,float localHostProgress, String mp3Path, int selectionNum) {
		OSCMessage message = new OSCMessage("/mp3");
		message.addArgument(uploadRate);
		message.addArgument(downloadRate);
		message.addArgument(localHostProgress);
		message.addArgument(mp3Path);
		message.addArgument(selectionNum);
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void sendLFMp3(float uploadRate,float downloadRate,float localHostProgress, String mp3Path, int selectionNum) {
		OSCMessage message = new OSCMessage("/LFmp3");
		message.addArgument(uploadRate);
		message.addArgument(downloadRate);
		message.addArgument(localHostProgress);
		message.addArgument(mp3Path);
		message.addArgument(selectionNum);
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void stopMp3(int selectionNum) {
		OSCMessage message = new OSCMessage("/stopMp3");
		message.addArgument(selectionNum);
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void sendDownloadDirectory(String downloadDirectory) {
		OSCMessage message = new OSCMessage("/downloadDirectory");
		message.addArgument(downloadDirectory);
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void sendTorrentFiles(float[] progresses) {
		OSCMessage message = new OSCMessage("/torrentFiles");
		for(int i=0;i<progresses.length;i++) {
			message.addArgument(progresses[i]);
		}
		
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public void resetSC() {
		OSCMessage message = new OSCMessage("/restart");
		message.addArgument("restart");
		try {
			oscPortOut.send(message);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
}
