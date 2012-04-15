package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OSXExec {
	int exitVal;
	
	public float exec(String string) {
		try{
			 Runtime rt = Runtime.getRuntime();
	            System.out.println("Execing " + string);
	            Process proc = rt.exec(string);
	            // any error message?
	            StreamGobbler errorGobbler = new 
	                StreamGobbler(proc.getErrorStream(), "ERROR");            
	            
	            // any output?
	            StreamGobbler outputGobbler = new 
	                StreamGobbler(proc.getInputStream(), "OUTPUT");
	                
	            // kick them off
	            errorGobbler.start();
	            outputGobbler.start();
	            	            
	            // any error???
	            exitVal = proc.waitFor();
	            System.out.println("ExitValue: " + exitVal);    
	            
	            
	        } catch (Throwable t) {
	            t.printStackTrace();
	        }
	        return exitVal;
	}
	
	
	//STREAM GOBBLER
	protected class StreamGobbler extends Thread{
		InputStream is;
		String type;
		 
		protected StreamGobbler(InputStream is,String type) {
			 this.is = is;
			 this.type = type;
		}
		
		public void run() {
			try {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	                System.out.println(type + ">" + line);    
	            } catch (IOException ioe) {
	                ioe.printStackTrace();  
	            }
	    }
	}

}
