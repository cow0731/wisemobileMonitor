package kr.co.deotis.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MntProperties {
	
	@SuppressWarnings("unused")
	private final int ZIP_BUFFER_SIZE = 4096;
	
	public MntProperties() {
		
    }
    
    private Properties readProperties(String configFile) throws IOException  { 
		Properties tempProperties = new Properties();
		FileInputStream in = new FileInputStream(configFile);
		tempProperties.load(in);
		in.close();
		
		return tempProperties;
  	}
	
    /**
     * read ini file
     * 
     * @param configFile : ini file name
     * @param keyName
     * @param defaultValue
     * @return
     */
	public String getPropertiesString (String configFile, String keyName, String defaultValue) {
		try {
			File file = new File(configFile);
			if (file.exists ()) {
				Properties prop = readProperties(configFile);
				return prop.getProperty(keyName);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Properties file not found. \r\n" + e.getMessage());
		} catch (IOException e) {
			System.out.println("properties read error. \r\n" + e.getMessage());
		} catch (Exception e) {
			System.out.println("error. \r\n" + e.getMessage());
		}
		
		return defaultValue;
	}
	
	public String getPropertiesString (String charset, String configFile, String keyName, String defaultValue) {
		try {
			File file = new File(configFile);
			if (file.exists ()) {
				Properties prop = readProperties(configFile);
				return new String(prop.getProperty(keyName).getBytes("ISO-8859-1"), charset);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Properties file not found. \r\n" + e.getMessage());
		} catch (IOException e) {
			System.out.println("properties read error. \r\n" + e.getMessage());
		} catch (Exception e) {
			System.out.println("error. \r\n" + e.getMessage());
			e.printStackTrace();
		}
		
		return defaultValue;
	}
	
	/**
	 * write ini file
	 * 
	 * @param configFile : ini file name
	 * @param keyName
	 * @param value
	 */
	public void setProperties (String configFile, String keyName, String value) {
		Properties prop=null;
		try {
			File file = new File(configFile);
			if (!file.exists ()) {
				file.createNewFile();
			}
			prop = readProperties(configFile);
			prop.setProperty(keyName, value);
			prop.store(new FileOutputStream(configFile), null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
}
  

