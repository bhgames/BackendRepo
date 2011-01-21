package BHEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RevClassLoader extends ClassLoader {

	String username;
	ClassLoader parent;
	public RevClassLoader(ClassLoader parent, String username) {
		this.parent=parent;

		this.username=username;
		// TODO Auto-generated constructor stub
	}
	
	 public Class loadClass(String name) throws ClassNotFoundException {
	        if(!name.startsWith("Revelations."))
	                return parent.loadClass(name);
	        try {
	        	
	            String url = PlayerScript.bindirectory;
	            int length = PlayerScript.dotCount(name);
	            int i = 0; String hp = new String(name);
	            while(i<length) {
	            	url+=hp.substring(0,hp.indexOf("."))+"/";
	            	hp = hp.substring(hp.indexOf(".")+1,hp.length());
	            	i++;
	            }
	            
	            url+=hp+".class";
	            URL myUrl = new URL("file:"+url);
	            URLConnection connection = myUrl.openConnection();
	            InputStream input = connection.getInputStream();
	            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	            int data = input.read();

	            while(data != -1){
	                buffer.write(data);
	                data = input.read();
	            }

	            input.close();

	            byte[] classData = buffer.toByteArray();

	            return defineClass(name,
	                    classData, 0, classData.length);

	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace(); 
	        }

	        return null;
	        

	 }

}
