package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Test;

import hu.barbar.util.FileHandler;
import junit.framework.TestSuite;

public class ConfigTest extends TestSuite {
	
	private static String configJsonFilePath = "..\\sample JSONs\\baseConfig_For_Test.json";
	
	JSONObject json = null;
	
	//System.out.println(FileHandler.readJSON(configJsonFilePath));
	
	
	@Test
	public void read_NON_EXISTING_config_from_JSON_Test(){
		
		Config.setConfigSourceJSON(configJsonFilePath);
		
		String jsonPathOfWantedValue = "non-existing-key";
		
		String value = (String) Config.getConfig(jsonPathOfWantedValue);
		assertEquals(null, value);
		
	}
	
	@Test
	public void read_single_level_config_value_from_from_config_JSON_Test(){
		
		Config.setConfigSourceJSON(configJsonFilePath);
		
		String jsonPathOfWantedValue = "testkey";
		
		String value = (String) Config.getConfig(jsonPathOfWantedValue);
		assertEquals("good_value", value);
		
	}
	
	@Test
	public void read_multi_level_config_value_from_from_config_JSON_Test(){
		
		Config.setConfigSourceJSON(configJsonFilePath);
		
		String jsonPathOfWantedValue = "loglevels.stdout";
		String value = (String) Config.getConfig(jsonPathOfWantedValue);
		assertEquals("info", value);
		
		jsonPathOfWantedValue = "loglevels.fileout";
		value = (String) Config.getConfig(jsonPathOfWantedValue);
		assertEquals("warn", value);
		
	}
	
}