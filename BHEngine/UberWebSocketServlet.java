package BHEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocket.FrameConnection;
import org.json.JSONException;
import org.json.JSONStringer;
public class UberWebSocketServlet extends WebSocketServlet {
	ArrayList<UberWebSocket> sockets = new ArrayList<UberWebSocket>();
	GodGenerator God;
	public WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		// TODO Auto-generated method stub
		UberWebSocket s = new UberWebSocket(sockets,God);
		sockets.add(s);
		return s;
	}
	public UberWebSocketServlet(GodGenerator g) {
		this.God=g;
	}

}
class UberWebSocket implements WebSocket, WebSocket.OnFrame, WebSocket.OnBinaryMessage, WebSocket.OnTextMessage, WebSocket.OnControl
{
    protected FrameConnection _connection;
    boolean _verbose = true;
    ArrayList<UberWebSocket> sockets;
    GodGenerator God;
    String username; int pid;
    public UberWebSocket(ArrayList<UberWebSocket> sockets, GodGenerator God) {
    	this.sockets=sockets;
    	this.God=God;
    }
    public FrameConnection getConnection()
    {
        return _connection;
    }
    
    public void onOpen(Connection connection)
    {
        if (_verbose)
            System.err.printf("%s#onOpen %s\n",this.getClass().getSimpleName(),connection);
    }
    
    public void onHandshake(FrameConnection connection)
    {
        if (_verbose)
            System.err.printf("%s#onHandshake %s %s\n",this.getClass().getSimpleName(),connection,connection.getClass().getSimpleName());
        _connection = connection;
    }

    public void onClose(int code,String message)
    {
        if (_verbose)
            System.err.printf("%s#onDisonnect %d %s\n",this.getClass().getSimpleName(),code,message);
        sockets.remove(this);
        
    }
    
    public boolean onFrame(byte flags, byte opcode, byte[] data, int offset, int length)
    {            
        if (_verbose)
            System.err.printf("%s#onFrame %s|%s %s\n",this.getClass().getSimpleName(),TypeUtil.toHexString(flags),TypeUtil.toHexString(opcode),TypeUtil.toHexString(data,offset,length));
        return false;
    }

    public boolean onControl(byte controlCode, byte[] data, int offset, int length)
    {
        if (_verbose)
            System.err.printf("%s#onControl  %s %s\n",this.getClass().getSimpleName(),TypeUtil.toHexString(controlCode),TypeUtil.toHexString(data,offset,length));            
        return false;
    }

    public void onMessage(String data)
    {
        if (_verbose)
            System.err.printf("%s#onMessages     %s\n",this.getClass().getSimpleName(),data);
        	Hashtable r = splitStringIntoHashtable(data);
        	r.put("pid",pid);
        	r.put("username",username);
        	
        	String id = (String) r.get("id");
        	UberSocketPrintWriter out = new UberSocketPrintWriter(_connection,null,null,r);
        	JSONStringer j = new JSONStringer();
        	try {
				j.object().key("id").value(id);
				out.println(j.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	God.doReqtypeSorting(out);
        	out.println("}");
    }
    
    public void sendMessage(String fakeURL) {
    	Hashtable r = splitStringIntoHashtable(fakeURL);
    	String type =(String) r.get("type");
    	UberSocketPrintWriter out = new UberSocketPrintWriter(_connection,null,null,r);

    	JSONStringer j = new JSONStringer();
    	try {
			j.object().key("type").value(type);
			out.println(j.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	God.doReqtypeSorting(out);
		out.println("}");
    }

    private Hashtable splitStringIntoHashtable(String data) {
    	Hashtable r = new Hashtable();
		// TODO Auto-generated method stub
    	if(!data.contains("&")) {
    		String name = data.substring(0,data.indexOf("="));
    		String fact = data.substring(data.indexOf("=")+1,data.length());
    		r.put(name,fact);
    		return r;
    	}
    	
    	while(data.contains("&")) {
			String name = data.substring(0,data.indexOf("="));
    		String fact = data.substring(data.indexOf("=")+1,data.indexOf("&"));
    		r.put(name,fact);
    		data = data.substring(data.indexOf("&")+1,data.length());
			
		} // so when it runs out of &'s, that means only one field is left.
    	String name = data.substring(0,data.indexOf("="));
		String fact = data.substring(data.indexOf("=")+1,data.indexOf("&"));
		r.put(name,fact);
		
		return r;
	}
	public void onMessage(byte[] data, int offset, int length)
    {
        if (_verbose)
            System.err.printf("%s#onMessage     %s\n",this.getClass().getSimpleName(),TypeUtil.toHexString(data,offset,length));
    }
}