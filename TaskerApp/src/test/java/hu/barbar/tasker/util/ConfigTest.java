package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Test;

import hu.barbar.util.FileHandler;
import junit.framework.TestSuite;

public class ConfigTest extends TestSuite {
	
	private static String configJsonFilePath = "..\\onTarget\\home_-_pi\\taskerData\\baseConfig.json";
	
	JSONObject json = null;
	
	//System.out.println(FileHandler.readJSON(configJsonFilePath));
	
	
	@Test
	public void readConfigFromJSONTest1(){
		
		Config.setConfigSourceJSON(configJsonFilePath);
		String jsonPathOfWantedValue = "loglevels.stdout";
		
		String value = (String) Config.getConfig(jsonPathOfWantedValue);
		assertEquals("info", value);
		
	}
	
}