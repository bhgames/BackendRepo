package BHEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    String totalRead = "";
    boolean done = false;
    
   public StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                totalRead+=type + ">" + line+"\n";    
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
            
            done = true;
    }
    
    public boolean isDone() {
    	return done;
    }
    public String returnRead() {
    	return totalRead;
    }
    
    
}
