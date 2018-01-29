package kr.co.deotis.socket;

import java.io.IOException;
import java.net.URL;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.deotis.config.MntProperties;
import lombok.Getter;
import lombok.Setter;

@ServerEndpoint("/monitor")
public class MonitorWebsocket {
	
	Logger logger = LoggerFactory.getLogger("Main");
	
	@Getter private Session session;
	@Setter @Getter private String TIME;
	@Setter @Getter private String SCODE;
	@Setter @Getter private String MSG;
	@Setter @Getter private boolean mService;
	
	private MonitorClient mc = null;
	
	public MonitorWebsocket() {
		getSession();
	}
	
	@OnOpen
	public void echoOpen(Session session) throws IOException {
		mService = true;
		logger.debug("websocket open!!");
		
		MntProperties prop = new MntProperties();
		
		URL confile = Thread.currentThread().getContextClassLoader().getResource("property.ini");
		String filePath = confile.getPath().substring(1);

		if(filePath.contains("build")) {		// eclipse
			filePath = filePath.replace("build/classes", "WebContent/WEB-INF/conf");
		} else if(filePath.contains("classes")) {	// tomcat
			filePath = filePath.replace("classes", "conf");
		}
		logger.debug("filepath : {}", filePath);
	
		String ip = prop.getPropertiesString(filePath, "serverIp", "192.168.0.53");
		int port = Integer.parseInt(prop.getPropertiesString(filePath, "serverport", "40011"));
		int sec = Integer.parseInt(prop.getPropertiesString(filePath, "sec", "5000"));
		
		logger.debug("serverIp : {}, serverPort : {}, sec : {}", ip, port, sec);
		
		mc = new MonitorClient(ip, port, sec, this, mService);
		mc.start();
		
		/*String[] params = session.getQueryString().split("&");
		String usr = params[0].split("=")[1];
		session.getUserProperties().put("user", usr);*/
		this.session = session;
	}
	
	public void sendToWeb(String msg) {
		try {
			if(session == null) {
				return;
			}
			else if(session.isOpen()) {
				session.getBasicRemote().sendText(msg);
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
		session.close();
		logger.debug("websocket close.");
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		
	}
}
