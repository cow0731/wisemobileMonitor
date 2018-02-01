package kr.co.deotis.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.deotis.config.MntProperties;
import kr.co.deotis.vo.WebSocketQueue;
import lombok.Getter;
import lombok.Setter;

@ServerEndpoint("/monitor")
public class MonitorWebsocket {
	
	Logger logger = LoggerFactory.getLogger("Main");
	
	@Getter static MonitorWebsocket mw = null;
	List<Session> users = Collections.synchronizedList(new ArrayList<>());
	
	@Getter private Session session;
	@Setter @Getter private String TIME;
	@Setter @Getter private String SCODE;
	@Setter @Getter private String MSG;
	@Setter @Getter private boolean mService = false;
	
	private MonitorClient mc = null;
	public WebSocketQueue queue = new WebSocketQueue();
	
	public static MonitorWebsocket getInstance() {
		if(mw == null) {
			mw = new MonitorWebsocket();
		}
		return mw;
	}
	
	/*public static void main(String[] args) {
		getInstance().init();
	}
	
	public void init() {
		users.clear();
		mService = true;
		MntProperties prop = new MntProperties();
		
		String filePath = prop.realPath("property.ini");
		logger.debug("filepath : {}", filePath);
	
		String ip = prop.getPropertiesString(filePath, "serverIp", "192.168.0.53");
		int port = Integer.parseInt(prop.getPropertiesString(filePath, "serverport", "40011"));
		int sec = Integer.parseInt(prop.getPropertiesString(filePath, "sec", "5000"));
		
		logger.debug("serverIp : {}, serverPort : {}, sec : {}", ip, port, sec);
		
		mc = new MonitorClient(ip, port, sec);
		mc.start();
	}*/
	
	@OnOpen
	public void echoOpen(Session session) throws IOException {
		mService = true;
		System.out.println(getMw().toString());
		queue.addSocketList(getMw());
		logger.debug("websocket open!!");
		
		//this.session = session;
		users.add(session);
		System.out.println("유저 사이즈: "+users.size());
		//war file in runnable java
	}
	
	public void sendToWeb(String msg) {
		
		Iterator<Session> iter = users.iterator();
		session = iter.next();
		try {
			if(session == null) {
				System.out.println("세션없음");
				return;
			}
			else if(session.isOpen()){
				session.getBasicRemote().sendText(msg);
				System.out.println("세션 오픈함.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				session.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@OnMessage
	public void receiveTextMessage(Session session, String msg, boolean last) {
		
		logger.debug("receive message flag : {}", msg);
		
	}
	
	@OnClose
	public void onClose(Session session) throws IOException {
		mService=false;
		users.remove(session);
		session.close();
		logger.debug("websocket close.");
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		
	}
}
