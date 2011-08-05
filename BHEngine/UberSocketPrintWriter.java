package BHEngine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.websocket.WebSocket.Connection;

public class UberSocketPrintWriter {
	Connection c;
	PrintWriter out;
	HttpServletRequest req;
	Hashtable r;
	public UberSocketPrintWriter(Connection c, PrintWriter out, HttpServletRequest req, Hashtable latentProperties) {
		this.c=c;
		this.out=out;
		this.req=req;
		this.r=latentProperties;
	}
	
	public void println(String toPrint) {
		if(c==null) out.println(toPrint);
		else
			try {
				c.sendMessage(toPrint);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void print(String toPrint) {
		if(c==null) out.println(toPrint);
		else
			try {
				c.sendMessage(toPrint);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void close() {
		if(out!=null) out.close();
	}
	public Object getAttribute(String attribute) {
		if(req!=null){
			return req.getSession().getAttribute(attribute);
		} 
		else {
			return r.get(attribute);
		}
	}
	public String getParameter(String parameter) {
		if(req!=null){ // seems sessions use attributes, the req itself prefers parameters. not sure of the diff.
			return (String) req.getParameter(parameter);
		} 
		else {
			return (String) r.get(parameter);
		}
	}
	
	public HttpSession getSession(boolean b) {
		// TODO Auto-generated method stub
		if(req!=null) {
			return req.getSession(b);
		}else
		return null;
	}
	
	
}
