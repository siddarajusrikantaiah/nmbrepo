package gov.nmb.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;


public class ConfigUtil {
	private static Map<String, String> configMap = new HashMap<String, String>();
	
	static{
		loadProperties();
	}
	
	private static void loadProperties(){		
		try {
			FileInputStream propsFile = new FileInputStream("config.properties");
			PropertyResourceBundle rb = new PropertyResourceBundle(propsFile);
			Enumeration keys = rb.getKeys();
			String Key;
			String Val;
			while (keys.hasMoreElements()) {
				Key = (String) keys.nextElement();
				Val = rb.getString(Key);
				if (Val != null) {
					Val = Val.trim();
				}
				configMap.put(Key, (String)Val);
			}
		} catch (IOException e) {
			System.out.println("Error in loading Properties ===================  "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String prop){
		return configMap.get(prop);
	}
	
	public static void main(String[] args) {
		System.out.println(ConfigUtil.getProperty("BUCKETNAME"));
	}

}
