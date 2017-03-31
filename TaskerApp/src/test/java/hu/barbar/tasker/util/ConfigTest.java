package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class ConfigTest extends TestSuite {

	private static String configJsonFilePath = "..\\sample JSONs\\baseConfig_For_Test.json";

	JSONObject json = null;

	
	@Before
	public void before(){
		//System.out.println(FileHandler.readJSON(configJsonFilePath));
	}

	@Test
	public void read_NON_EXISTING_config_from_JSON_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);

		String jsonPathOfWantedValue = "non-existing-key";

		String value = (String) Config.getConfigWithoutDefault(jsonPathOfWantedValue);
		assertEquals(null, value);

	}

	@Test
	public void read_single_level_config_value_from_config_JSON_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);

		String jsonPathOfWantedValue = "testkey";

		String value = (String) Config.getConfigWithoutDefault(jsonPathOfWantedValue);
		assertEquals("good_value", value);

	}

	@Test
	public void read_multi_level_config_value_from_config_JSON_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);

		String jsonPathOfWantedValue = "log.levels.stdout";
		String value = (String) Config.getConfigWithoutDefault(jsonPathOfWantedValue);
		assertEquals("info", value);

		jsonPathOfWantedValue = "log.levels.fileout";
		value = (String) Config.getConfigWithoutDefault(jsonPathOfWantedValue);
		assertEquals("warn", value);

	}
	
	
	@Test
	public void read_value_from_config_JSON_where_the_key_contains_space_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);
		
		String jsonPathOfWantedValue = "an other key with space";
		String value = Config.getConfigStrWithoutDefault(jsonPathOfWantedValue);
		assertEquals("yes, key can contains space", value);

	}
	
	@Test
	public void read_int_value_from_config_JSON_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		int defaultValue = 600;
		int value = Config.getInt("devices.fan.start boost time in ms", defaultValue);
		assertEquals(800, value);
	}
	
	@Test
	public void read_int_value_from_config_JSON_NON_EXISTING_KEY_returns_default_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		int defaultValue = 600;
		int value = Config.getInt("non.existing key", defaultValue);
		assertEquals(defaultValue, value);
	}
	
	@Test
	public void read_int_value_from_config_JSON_Key_is_NULL_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		int defaultValue = 601;
		int value = Config.getInt(null, defaultValue);
		assertEquals(defaultValue, value);
	}
	
	@Test
	public void read_int_value_from_config_JSON_Key_has_non_numeric_value_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		int defaultValue = 601;
		int value = Config.getInt("testkey", defaultValue);
		assertEquals(defaultValue, value);
	}

}
