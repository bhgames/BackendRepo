package BHEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class TextDemo extends WebSocketServlet {
	Controllers Router; 
	public TextDemo(Controllers Router) {
		
		this.Router=Router;
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws IOException, ServletException {

	
	//res.setContentType("text/html");
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	if(true||req.getParameter("reqtype").equals("restartServer")) 
	if(req.getParameter("reqtype").equals("world_map")) {
		Router.loadWorldMap(req,out);
	} else if(req.getParameter("reqtype").equals("forgotPass")) {
		Router.forgotPass(req,out);
	}else if(req.getParameter("reqtype").equals("serverStatus")) {
		Router.serverStatus(req,out);
	}else if(req.getParameter("reqtype").equals("convert")) {
		Router.convert(req,out);
	}else if(req.getParameter("reqtype").equals("stopServer")) {
		Router.stopServer(req,out);
	}else if(req.getParameter("reqtype").equals("runTest")) {
		Router.runTest(req,out);
	}else if(req.getParameter("reqtype").equals("deleteOldPlayers")) {
		Router.deleteOldPlayers(req,out);
	}else if(req.getParameter("reqtype").equals("returnPrizeName")) {
		Router.returnPrizeName(req,out);
	}else if(req.getParameter("reqtype").equals("newsletter")) {
		Router.newsletter(req,out);
	}else if(req.getParameter("reqtype").equals("repairMap")) {
		Router.repairMap(req,out);
	}else if(req.getParameter("reqtype").equals("player")) {
		Router.loadPlayer(req,out,false);
	} else if(req.getParameter("reqtype").equals("league")) {
		Router.loadPlayer(req,out,true);
	}  else if(req.getParameter("reqtype").equals("login")) {
		Router.login(req,out);
	}  else if(req.getParameter("reqtype").equals("makePlayers")) {
		Router.makePlayers(req,out);
	} else if(req.getParameter("reqtype").equals("FBBlast")) {
		Router.FBBlast(req,out);
	}else if(req.getParameter("reqtype").equals("logout")) {
		Router.logout(req,out);
	}else if(req.getParameter("reqtype").equals("noFlick")) {
		Router.noFlick(req,out);
	}else if(req.getParameter("reqtype").equals("flickStatus")) {
		Router.flickStatus(req,out);
	}else if(req.getParameter("reqtype").equals("deleteAccount")) {
		Router.deleteAccount(req,out);
	}else if(req.getParameter("reqtype").equals("getTiles")) {
		Router.getTiles(req,out);
	} else if(req.getParameter("reqtype").equals("getZongScreen")) {
		Router.getZongScreen(req,out);
	} else if(req.getParameter("reqtype").equals("upgrade")) {
		Router.upgrade(req,out);
	}else if(req.getParameter("reqtype").equals("pausePlayer")) {
		Router.pausePlayer(req,out);
	} else if(req.getParameter("reqtype").equals("syncPlayer")) {
		Router.syncPlayer(req,out);
	}else if(req.getParameter("reqtype").equals("makePaypalReq")) {
		Router.makePaypalReq(req,out);
	} else if(req.getParameter("reqtype").equals("saveServer")) {
		Router.saveServer(req,out);
	}else if(req.getParameter("reqtype").equals("session")) {
		Router.session(req,out,false);
	} else if(req.getParameter("reqtype").equals("command")) {
		Router.command(req,out);
	} else if(req.getParameter("reqtype").equals("tileset")) {
		Router.growTileset(req,out);
	}else if(req.getParameter("reqtype").equals("username")) {
		Router.username(req,out);
	}else if(req.getParameter("reqtype").equals("growId")) {
		Router.growId(req,out);
	}else if(req.getParameter("reqtype").equals("createNewPlayer")) {
		Router.createNewPlayer(req,out);
	}else if(req.getParameter("reqtype").equals("generateCodes")) {
		Router.generateCodes(req,out);
	} else if(req.getParameter("reqtype").equals("deletePlayer")) {
		Router.deletePlayer(req,out);
	} else if(req.getParameter("reqtype").equals("restartServer")) {
		Router.restartServer(req,out);
	} else if(req.getParameter("reqtype").equals("sendTestEmail")) {
		Router.sendTestEmail(req,out);
	}  else	if(req.getParameter("reqtype").equals("compileProgram")) {
		Router.compileProgram(req,out);
	} 
	else {
		
		out.println("BLAH");
	}
	else 
		out.println("BLAH");

	out.close();
}
	public WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		// TODO Auto-generated method stub
		return new TestWebSocket();
	}
	
}
class TestWebSocket implements WebSocket, WebSocket.OnFrame, WebSocket.OnBinaryMessage, WebSocket.OnTextMessage, WebSocket.OnControl
{
    protected FrameConnection _connection;
    boolean _verbose = true;
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
            System.err.printf("%s#onMessagesss     %s\n",this.getClass().getSimpleName(),data);
        try {
			getConnection().sendMessage(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

    public void onMessage(byte[] data, int offset, int length)
    {
        if (_verbose)
            System.err.printf("%s#onMessage     %s\n",this.getClass().getSimpleName(),TypeUtil.toHexString(data,offset,length));
    }
}

