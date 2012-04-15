Leech {
	var responder,netaddr,synthSelectionArray,synthArray,paramArray,synthDic,processing,server;
	var mp3s,mp3Buffers,mp3Synths,downloadDirectory,mp3Names;
	var monoReceiveSynth, monoSendSynth, monoReceiveSeederSynth, monoSendSeederSynth;
	var monoReceiveSynthBuf, monoSendSynthBuf, monoReceiveSeederSynthBuf, monoSendSeederSynthBuf,torrentFiles=nil;
	var bufNum = 64;
	
	*new{
		^super.new.init()
	}
	
	
	init{
		var options;
		options = ServerOptions.new();
		options.memSize = 8192 * 20;
		netaddr = NetAddr("127.0.0.1", 57120);
		processing = NetAddr("127.0.0.1",12000);
		server = Server.default;
		this.compileSynthDefs;
		options.maxNodes_(64000);
		mp3s = Array.newClear(0);
		mp3Buffers = Array.newClear(0);
		mp3Synths = Array.newClear(0);
		mp3Names = Array.newClear(0);
		monoReceiveSynth = Synth(\BufferMonoReceive);
		monoSendSynth = Synth(\BufferMonoReceive);
		monoReceiveSeederSynth = Synth(\BufferMonoReceive);
		monoSendSeederSynth = Synth(\BufferMonoReceive);
		torrentFiles = nil;
		bufNum = 64;
		responder = OSCresponderNode(nil, '/receivedPacket', {
	 		|t, r, msg| 
	 		//msg.postln;
	 		if(server.peakCPU<80, {
		 		if(msg[4]<0.99,{
			 		monoReceiveSynth.set(\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]);
			 		//Synth(\SyncReceive,[\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]]);
		 		},{
			 		monoSendSynth.set(\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]);
			 		//Synth(\SyncReceive,[\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]]);
		 		})	 		
		 	});
	 	}).add;
	 	
	 	responder = OSCresponderNode(nil, '/sentPacket', {
	 		|t, r, msg| 
	 		//msg.postln;
	 		if(server.peakCPU<80, {
		 		if(msg[4]<0.99,{
			 		monoReceiveSeederSynth.set(\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]);
			 		//Synth(\SyncReceive,[\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]]);
		 		},{
			 		monoSendSeederSynth.set(\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]);
			 		//Synth(\SyncReceive,[\downloadRate,msg[1],\lat,msg[2],\lon,msg[3],\nodeProg,msg[4],\localProg,msg[5]]);
		 		})
		 		
			});
	 	}).add;
	 	
	 	responder = OSCresponderNode(nil, '/mp3', {
	 		|t, r, msg| 
	 		msg.postln;
	 		[downloadDirectory++"/"++msg[4]].postln;
	 		mp3s = mp3s.add(MP3(downloadDirectory++"/"++msg[4]));
	 		mp3Buffers = mp3Buffers.add(MP3.readToBuffer(server,downloadDirectory++"/"++msg[4]));
	 		mp3Synths = mp3Synths.add(Synth(\MP3Player,[\downloadRate,msg[1],\localProg,msg[3],\mp3,mp3s[mp3s.size-1]]));
	 	}).add;
	 	
	 	responder = OSCresponderNode(nil, '/downloadDirectory', {
	 		|t, r, msg| 
	 		msg.postln;
	 		downloadDirectory = msg[1];
	 		downloadDirectory.postln;
	 	}).add;
	 	
	 	responder = OSCresponderNode(nil, '/torrentFiles', {
	 		|t, r, msg| 
	 		//msg.postln;
	 		if(torrentFiles==nil,{
		 		torrentFiles = Array.newClear(msg.size-1);
		 		(msg.size-1).do({|i|
			 		torrentFiles[i] = Synth(\TorrentFileDrone,[\fileProgress,msg[i+1]]);
		 		});
	 		});
	 		(msg.size-1).do({|i|
				torrentFiles[i].set(\fileProgress,msg[i+1]);
		 	});
	 	}).add;
	}
	
	restart{
		monoReceiveSynth.set(\gate,1);
		monoSendSynth.set(\gate,1);
		monoReceiveSeederSynth.set(\gate,1);
		monoSendSeederSynth.set(\gate,1);
		
		(torrentFiles.size).do({|i|
			torrentFiles[i].set(\gate,1);
		});
		torrentFiles = nil;
		monoReceiveSynth = Synth(\BufferMonoReceive);
		monoSendSynth = Synth(\BufferMonoReceive);
		monoReceiveSeederSynth = Synth(\BufferMonoReceive);
		monoSendSeederSynth = Synth(\BufferMonoReceive);	}
	
	compileSynthDefs {
		SynthDef.new(\LeechDefaultReceive,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.01,0.02,0.03],0),doneAction:2);
			osc1 = SinOsc.ar(lat.linlin(-100,100,0.05,30).abs,0,1000*nodeProg);
			//osc2 = Pulse.ar(lon.linlin(-100,100,50,3000).abs,0.5,1);
			osc2 = LFSaw.ar(lon.linlin(-100,100,0,1000).abs.trunc(100)*0.5,downloadRate*0.01%1,1);
			//osc2 = SyncOsc.ar(lon.linlin(-100,100,75,300).abs,lat.linlin(-100,100,75,300).abs,0,2,1);
			signal = osc2*env;
			Out.ar(0,[signal,signal]*0.002);
			}
		).store;
		
		SynthDef.new(\LeechDefaultSend,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.01,0.02,0.03],0),doneAction:2);
			osc1 = SinOsc.ar(lat.linlin(-100,100,0.05,30).abs,0,4000*nodeProg);
			//osc2 = Pulse.ar(lon.linlin(-100,100,50,3000).abs,0.5,1);
			osc2 = LFSaw.ar(lon.linlin(-100,100,0,1000).abs.trunc(100),downloadRate*0.01%1,1);
			//osc2 = SyncOsc.ar(lon.linlin(-100,100,75,300).abs,lat.linlin(-100,100,75,300).abs,0,2,1);
			signal = osc2*env;
			Out.ar(0,[signal,signal]*0.002);
			}
		).store;
		
		SynthDef.new(\LeechDefaultReceiveSeeder,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.05,0.01,2],-6),doneAction:2);
			osc1 = SinOsc.ar(lat.linlin(-100,100,75,50).abs.trunc(100),0,5*nodeProg*downloadRate*0.1);
			//osc2 = Pulse.ar(lon.linlin(-100,100,50,3000).abs,0.5,1);
			osc2 = LFPulse.ar(lon.linlin(-100,100,50,3000).abs.trunc(100),0.5,0.5,1);
			//osc2 = osc2 + SyncOsc.ar(lon.linlin(-100,100,5,3000).abs.trunc(50)*0.25,lat.linlin(-100,100,5,3000).abs.trunc(100)*0.125,0,0,1);
			signal = osc2*env;
			Out.ar(0,[signal,signal]*0.005);
			}
		).store;
		
		SynthDef.new(\LeechDefaultSendSeeder,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.05,0.01,2],-6),doneAction:2);
			osc1 = SinOsc.ar(lat.linlin(-100,100,75,50).abs.trunc(100),0,5*nodeProg*downloadRate*0.1);
			//osc2 = Pulse.ar(lon.linlin(-100,100,50,3000).abs,0.5,1);
			osc2 = LFTri.ar(lon.linlin(-100,100,50,4000).abs.trunc(100),0.5,1);
			//osc2 = osc2 + SyncOsc.ar(lon.linlin(-100,100,5,3000).abs.trunc(25)*0.25,lat.linlin(-100,100,5,3000).abs.trunc(100)*0.125,0,0,1);
			signal = osc2*env;
			Out.ar(0,[signal,signal]*0.01);
			}
		).store;
		
		SynthDef.new(\NESReceive,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.02,0.01],-3),doneAction:2);
			osc1 = Nes2Square.ar(
				Impulse.ar(0.1),//trig
				nodeProg%3,//dutyCycle
				0,//loopenv
				1,//envdecay
				15,//volume
				0,//sweep
				downloadRate%7,//sweeplen
				lat.linlin(-150,150,0,1).abs,//sweepdir
				nodeProg*7,//sweepshift
				lon.linlin(-150,150,0,2000).round(200),//frequency
				31//vbl-lengthcounter
			);
			signal = osc1*env;
			Out.ar(0,[signal,signal]*0.01);
			}
		).store;
		
		SynthDef.new(\NESReceiveSeeder,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.1,0.22],-2),doneAction:2);
			osc1 = Nes2Square.ar(
				Impulse.ar(0.1),//trig
				downloadRate%3,//dutyCycle
				0,//loopenv
				1,//envdecay
				15,//volume
				0,//sweep
				downloadRate%7,//sweeplen
				lat.linlin(-150,150,0,1).abs,//sweepdir
				nodeProg*7,//sweepshift
				lon.linlin(-150,150,0,1000).round(100),//frequency
				31//vbl-lengthcounter
			);
			signal = osc1*env;
			Out.ar(0,[signal,signal]*0.4);
			}
		).store;
		
		SynthDef.new(\NESSendSeeder,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.1,0.22],-2),doneAction:2);
			osc1 = Nes2Square.ar(
				Impulse.ar(0.1),//trig
				downloadRate%3,//dutyCycle
				0,//loopenv
				1,//envdecay
				15,//volume
				0,//sweep
				downloadRate%7,//sweeplen
				lat.linlin(-150,150,0,1).abs,//sweepdir
				nodeProg*7,//sweepshift
				lon.linlin(-150,150,0,1000).round(100),//frequency
				31//vbl-lengthcounter
			);
			signal = osc1*env;
			Out.ar(0,[signal,signal]*0.2);
			}
		).store;
		
		SynthDef.new(\SyncReceive,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env,feed;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.002,0.01],-5),doneAction:2);
			osc1 = Saw.ar(lon.linlin(-150,150,0,2000).round(50),0.2);
			osc1 = BPF.ar(osc1,lat.linlin(-150,150,20,2000),0.01);
			signal = osc1*env;
			Out.ar(0,[signal,signal]*0.4);
			}
		).store;
		
		SynthDef.new(\MP3Player,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0,mp3=0|
			var signal,osc1,osc2,env;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.05,1000,0.01],-5),doneAction:2);
			osc1 = PlayBuf.ar(1,mp3+bufNum,BufRateScale.kr(mp3)*0.25,loop:1,doneAction:0);
			//osc1 = TGrains.ar(2,Impulse.ar(120),mp3,1,Phasor.kr(0,BufRateScale.kr(mp3)*0.0001,0,BufDur.kr(mp3))+ TRand.kr(0, 0.01, 120),0.1,0,0.1,4);
			osc1 = (osc1*10).softclip;
			osc1 = FreeVerb.ar(osc1,0.5,0.9,0.9);
			signal = osc1*env;
			Out.ar(0,[signal,signal]*0.125);
			}
		).store;
		
		monoReceiveSynthBuf = Array.newClear(bufNum);
		bufNum.do({|i|
			monoReceiveSynthBuf[i] = Buffer.alloc(server,1024, 1);
		});
		bufNum.do({|i|
			var array,array2,array3,rand;
			
			rand = 3.rand;
			
			array = Array.newClear(256.rand+1);
				array.size.do({|i|
					array[i] = 0.500.rand;
				});
				monoReceiveSynthBuf[i].cheby(array);				case
			{rand == 0}{
				array = Array.newClear(256.rand+1);
				array.size.do({|i|
					array[i] = 0.500.rand;
				});
				monoReceiveSynthBuf[i].cheby(array);
			}
			{rand == 1}{
				array = Array.newClear(256.rand+12);
				array.size.do({|i|
					array[i] = 256.rand.round(1)*0.0625;
				});
				array2 = Array.newClear(256.rand+12);
				array2.size.do({|i|
					array2[i] = 0.500.rand+0.4;
				});
				array3 = Array.newClear(64.rand+12);
				array3.size.do({|i|
					array3[i] = 2pi.rand;
				});
				
				monoReceiveSynthBuf[i].sine3(array,array2,array3);
			}
			
			{rand == 2}{
				array = Array.newClear(256);
				array.size.do({|i|
					array[i] = 0.5.rand;
				});
				monoReceiveSynthBuf[i].setn(0,array);
			};
			
			
					 
		});
		
		bufNum.do({|i|
			//monoReceiveSynthBuf[i].plot;
		});
		
		SynthDef.new(\MonoReceiveSynth,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,env,feed,latTrig,lonTrig;
			var trigEnv,chain;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.002,0.01],-5,2,1),gate:gate,doneAction:2);
			latTrig = HPZ1.kr(lat) > 0;
			trigEnv = EnvGen.ar(Env.new([0.1,1.0,1.0,0.25],[0.025,0.025,2],-1),latTrig,doneAction:0);

			//UGens
			osc1 = Pulse.ar(lon.linlin(-150,150,10,1500).round(75) *0.5,lat.linlin(-150,150,0,1).abs %1,2) +
			Pulse.ar(lon.linlin(-150,150,10,2200).round(75),lat.linlin(-150,150,0,1).abs %1,2) + 
			Pulse.ar(lon.linlin(-150,150,10,2200).round(75) *0.25,lat.linlin(-150,150,0,1).abs %1,2) +
			Pulse.ar(lon.linlin(-150,150,10,2200).round(75) * 0.5,0.5,1);
			
			osc1 = FreeVerb.ar(osc1,0.3,0.9,0.9);
			signal = osc1*trigEnv;
			Out.ar(0,[signal,signal]*0.0125);
			}
		).store;
		
		SynthDef.new(\BufferMonoReceive,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,osc3,env,feed,latTrig,lonTrig;
			var trigEnv,mod,drand1,drand2,filter,chain,chain2;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.002,0.01],-5,2,1),gate:gate,doneAction:2);
			latTrig = HPZ2.kr(lat) > 0;
			trigEnv = EnvGen.ar(Env.new([1,0.5,0.5,1,1],[0.0005,0.005,0.1,0.1],1),latTrig,doneAction:0);
			//trigEnv = EnvGen.ar(Env.new([0,1,1,0.25],[0.05,0.05,2],-1),latTrig,doneAction:0);

			//UGens
			mod = LFNoise1.ar(10).abs;
			osc1 = VOsc.ar(
					TWindex.kr(latTrig, [
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
					]),
					lon.linlin(-150,150,10,1500).round(75)*[0.5,0.25], 0, 0.3)+
				VOsc.ar(
					TWindex.kr(latTrig, [
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
						1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
					]),
					lon.linlin(-150,150,10,1500).round(75)*0.5*[0.25,0.5], 0, 0.3);
						
			osc2 = GVerb.ar(
				osc1[0],
				200, 
				8, 
				0.8, 
				0.5, 
				5, 
				-1.dbamp,
				-9.dbamp, 
				-9.dbamp,
				200, 
				0.3
			);
			
			osc3 = GVerb.ar(
				osc1[1],
				200, 
				8, 
				0.8, 
				0.5, 
				5, 
				-1.dbamp,
				-9.dbamp, 
				-9.dbamp,
				200, 
				0.3
			);
			
			//signal = [osc2[0]+(osc3[0]*0.15),osc3[1]+(osc2[1]*0.15)]*trigEnv;
			signal = osc1*trigEnv;
			Out.ar(0,signal*0.3);
			}
		).store;
		
		SynthDef.new(\BufferMonoSend,{|amp=0.01,downloadRate,lat,lon,nodeProg,localProg,gate=0|
			var signal,osc1,osc2,osc3,env,feed,latTrig,lonTrig;
			var trigEnv,chain,mod;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[0.1,0.002,0.01],-5,2,1),gate:gate,doneAction:2);
			latTrig = HPZ2.kr(lat) > 0;
			trigEnv = EnvGen.ar(Env.new([0.1,1.0,1.0,0.1],[0.025,0.025,1],-10),latTrig,doneAction:0);

			//UGens
			osc1 = Nes2Noise.ar(
				latTrig,
				0,
				0,
				15,
				0,
				lon.linlin(-150,150,0,15), 
				lat.linlin(-150,150,0,31)
			);
			
			signal = osc1*trigEnv;
			Out.ar(0,[signal,signal]*0.4);
			}
		).store;
		
		SynthDef.new(\TorrentFileDrone,{|amp=0.01,fileProgress=0,gate=0|
			var signal,osc1,osc2,osc3,env,feed,latTrig,lonTrig;
			var trigEnv,chain,mod;
			
			env = EnvGen.ar(Env.new([0.0,1.0,1.0,0.0],[2,0.002,0.01],-5,2,1),gate:gate,doneAction:2);

			//UGens
			osc1 = [0,0];
			1.do({
				osc1[0] = osc1[0] + RLPF.ar(
						Gendy3.ar(2,3,freq:fileProgress.linlin(0,1,Rand(50,200),Rand(1400,1800)), durscale:0.0, initCPs:5),
						fileProgress.linexp(0,1,Rand(50,200),Rand(1400,1800)),
						0.1,
						0.2
				);
			});
			1.do({
				osc1[1] = osc1[1] + RLPF.ar(
						Gendy3.ar(2,3,freq:fileProgress.linlin(0,1,Rand(50,200),Rand(1400,1800)), durscale:0.0, initCPs:5),
						fileProgress.linexp(0,1,Rand(50,200),Rand(1400,1800)),
						0.1,
						0.2
				);
			});
			
			osc1 = FreeVerb.ar(osc1,0.5,1,0.95);
			signal = osc1;
			Out.ar(0,signal*0.1);
			}
		).store;
	}
}
/*
~leech = Leech.new;
~leech.restart;
*/