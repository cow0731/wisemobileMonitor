package kr.co.deotis.socket;

import kr.co.deotis.config.MntProperties;

public class MonitorServer {
	
	public static MonitorServer ms = null;
	public MonitorClient mc = null;
	String ip = null;
	int port = 0;
	
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
		
		mc = new MonitorClient(ip, port);
		mc.start();
	}
	
	public static void main(String[] args) {
		MonitorServer.getInstance();
	}
}
