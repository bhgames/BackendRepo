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
		String ipAddr = arg0.getRemoteAddr();

		for(Player p: God.getPlayers()) {
			if(p.socket!=null&&p.socket.ipAddr!=null&&p.socket.ipAddr.equals(ipAddr)) {
				return p.socket;
			}
		}
		return null;
	}
	public UberWebSocketServlet(GodGenerator g) {
		this.God=g;
	}
	public void sendMessage(int pid, String fakeURL) {
		System.out.println(pid + " wants shit.");
		for(UberWebSocket s:sockets) {
			if(s.player.ID==pid) {
				System.out.println("Delivering the shit.");
				s.sendMessage(fakeURL);
			}
		}
	}

}
class UberWebSocket implements WebSocket, WebSocket.OnFrame, WebSocket.OnBinaryMessage, WebSocket.OnTextMessage, WebSocket.OnControl
{
    protected FrameConnection _connection;
    boolean _verbose = true;
    ArrayList<UberWebSocket> sockets;
    Player player;
    String ipAddr=null;
    public UberWebSocket(ArrayList<UberWebSocket> sockets, Player player) {
    	this.sockets=sockets;
    	this.player=player;
    	sockets.add(this);
    	
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
        player.socket=new UberWebSocket(sockets,player);
        player.socket.ipAddr=ipAddr;
        
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
     	r.put("pid",player.ID);
    	r.put("username",player.getUsername());
    	
    	UberSocketPrintWriter out = new UberSocketPrintWriter(_connection,null,null,r);
    	System.out.println("Got it ready...");
        if(player.God.serverLoaded&&ipAddr!=null) {
        	System.out.println("Sending!");
		        	String id = (String) r.get("id");
		        	out.println("{'type':"+id + ",'data':");
					
		        	player.God.doReqtypeSorting(out);
		        	out.println("}");
        		//}
        	//}
        } else out.println("invalid");
    }
    
    public void sendMessage(String fakeURL) {
    	if(player.God.serverLoaded&&ipAddr!=null) {
	    	Hashtable r = splitStringIntoHashtable(fakeURL);
	    	r.put("pid",player.ID); r.put("username",player.getUsername());
	    	String type =(String) r.get("type");
	    	System.out.println("type is " + type + " connection is " + _connection);
	    	UberSocketPrintWriter out = new UberSocketPrintWriter(_connection,null,null,r);
	
	    	out.println("{'type':"+type + ",'data':");
			
        	player.God.doReqtypeSorting(out);
        	out.println("}");
    	}
    }

    private Hashtable splitStringIntoHashtable(String data) {
    	Hashtable r = new Hashtable();
		// TODO Auto-generated method stub
    	if(!data.contains("&")) {
    		String name = data.substring(0,data.indexOf("="));
    		String fact = data.substring(data.indexOf("=")+1,data.length());
    		System.out.println("Putting in " + name + "," + fact);
    		r.put(name,fact);
    		return r;
    	}
    	
    	while(data.contains("&")) {
			String name = data.substring(0,data.indexOf("="));
    		String fact = data.substring(data.indexOf("=")+1,data.indexOf("&"));
    		r.put(name,fact);
    		System.out.println("Putting in " + name + "," + fact);
    		data = data.substring(data.indexOf("&")+1,data.length());
			
		} // so when it runs out of &'s, that means only one field is left.
    	String name = data.substring(0,data.indexOf("="));
		String fact = data.substring(data.indexOf("=")+1,data.length());
		System.out.println("Putting in " + name + "," + fact);

		r.put(name,fact);
		
		return r;
	}
	public void onMessage(byte[] data, int offset, int length)
    {
        if (_verbose)
            System.err.printf("%s#onMessage     %s\n",this.getClass().getSimpleName(),TypeUtil.toHexString(data,offset,length));
    }
}