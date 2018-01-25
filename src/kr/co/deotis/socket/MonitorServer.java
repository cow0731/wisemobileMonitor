package kr.co.deotis.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.deotis.config.MntProperties;

public class MonitorServer {
	
	Logger logger = LoggerFactory.getLogger("Main");
	
	public static MonitorServer ms = null;
	public MonitorClient mc = null;
	String ip = null;
	int port = 0;
	int sec = 0;
	
	public static MonitorServer getInstance() {
		if(ms == null) {
			ms = new MonitorServer();
		}
		return ms;
	}
	
	public MonitorServer() {
		MntProperties prop = new MntProperties();
		
		ip = prop.getPropertiesString("property.ini", "ip", "192.168.0.53");
		port = Integer.parseInt(prop.getPropertiesString("property.ini", "port", "40011"));
		sec = Integer.parseInt(prop.getPropertiesString("property.ini", "sec", "5000"));
		
		logger.debug("ip : {}, port : {}, sec : {}", ip, port, sec);
		
		mc = new MonitorClient(ip, port, sec);
		mc.start();
	}
	
	public static void main(String[] args) {
		MonitorServer.getInstance();
	}
}
