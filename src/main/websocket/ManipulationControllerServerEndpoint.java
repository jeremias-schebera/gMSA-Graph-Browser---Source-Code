package main.websocket;

import java.io.IOException;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import main.Algorithms.DrawingController;
import main.Controller.ControlWindowController;

@ServerEndpoint("/manipulationController")
public class ManipulationControllerServerEndpoint {
	
	@OnOpen
    public Session open(Session session, EndpointConfig conf) {
        System.out.println("Manipulation Websocket server open: " + session);
        return session;
    }

    @OnMessage
    public void receiveMessage(String msg, Session session) {
    	
    }

    private void sendJSON(Session session, String jsonString) {
    	try {
			session.getBasicRemote().sendText(jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @OnClose
    public void close(Session session, CloseReason reason) {
        System.out.println("manipulation socket closed: "+ reason.getReasonPhrase());
    }

    @OnError
    public void error(Session session, Throwable t) {
        t.printStackTrace();
    }
}
