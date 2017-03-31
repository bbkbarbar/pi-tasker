package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

@SuppressWarnings("deprecation")
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

		String value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals(null, value);

	}

	@Test
	public void read_single_level_config_value_from_config_JSON_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);

		String jsonPathOfWantedValue = "testkey";

		String value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals("good_value", value);

	}

	@Test
	public void read_multi_level_config_value_from_config_JSON_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);

		String jsonPathOfWantedValue = "log.levels.stdout";
		String value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals("info", value);

		jsonPathOfWantedValue = "log.levels.fileout";
		value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals("warn", value);

	}
	
	
	@Test
	public void read_value_from_config_JSON_where_the_key_contains_space_Test(){

		Config.setConfigSourceJSON(configJsonFilePath);
		
		String jsonPathOfWantedValue = "an other key with space";
		String value = Config.getStringWithoutDefault(jsonPathOfWantedValue);
		assertEquals("yes, key can contains space", value);

	}
	
	/*
	 *  getInt
	 */
	
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
	
	
	/*
	 *  getFloat
	 */
	
	private boolean floatsEquals(float val1, float val2){
		float minimal_value = 0.001f;
		// Check that value2 is between [value1-minimal_value ; value1+minimal_value]
		return ( ((val1 - minimal_value) < val2) && ((val1 + minimal_value) > val2) );
	}
	
	@Test
	public void read_float_value_from_config_json_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		float defaultValue = 3.14f;
		float expected = 50.1f;
		float value = Config.getFloat("devices.heater.energy consumption in kwh", defaultValue);
		assertTrue(floatsEquals(expected, value));
	}

	@Test
	public void read_float_value_from_config_json_KEY_IS_NULL_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		float defaultValue = 3.14f;
		float value = Config.getFloat(null, defaultValue);
		assertTrue(floatsEquals(defaultValue, value));
	}
	
	@Test
	public void read_float_value_from_config_json_VALUE_LOOKS_LIKE_AN_INT_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		float defaultValue = 3.14f;
		float expected = 800f;
		float value = Config.getFloat("devices.fan.start boost time in ms", defaultValue);

		if(!floatsEquals(expected, value)){
			System.out.println("Expected: |" + expected + "|");
			System.out.println("Got:      |" + value + "|");
		}
		assertTrue(floatsEquals(expected, value));
	}
	
	@Test
	public void read_float_value_from_config_JSON_Key_has_non_numeric_value_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		float defaultValue = 7.13f;
		float value = Config.getFloat("testkey", defaultValue);
		assertTrue(floatsEquals(defaultValue, value));
	}
	
	@Test
	public void read_float_value_from_config_JSON_NON_EXISTING_KEY_returns_default_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		float defaultValue = 7.13f;
		float value = Config.getFloat("non.existing key", defaultValue);
		assertTrue(floatsEquals(defaultValue, value));
	}
	
	
	
	/*
	 *  getDouble
	 */

	private boolean doublesEquals(double val1, double val2){
		double minimal_value = 0.00001f;
		// Check that value2 is between [value1-minimal_value ; value1+minimal_value]
		return ( ((val1 - minimal_value) < val2) && ((val1 + minimal_value) > val2) );
	}

	@Test
	public void read_double_value_from_config_json_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		double defaultValue = 3.14f;
		double expected = 50.1f;
		double value = Config.getDouble("devices.heater.energy consumption in kwh", defaultValue);
		assertTrue(doublesEquals(expected, value));
	}

	@Test
	public void read_double_value_from_config_json_KEY_IS_NULL_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		double defaultValue = 3.14f;
		double value = Config.getDouble(null, defaultValue);
		assertTrue(doublesEquals(defaultValue, value));
	}

	@Test
	public void read_double_value_from_config_json_VALUE_LOOKS_LIKE_AN_INT_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		double defaultValue = 3.14f;
		double expected = 800f;
		double value = Config.getDouble("devices.fan.start boost time in ms", defaultValue);

		if(!doublesEquals(expected, value)){
			System.out.println("Expected: |" + expected + "|");
			System.out.println("Got:      |" + value + "|");
		}
		assertTrue(doublesEquals(expected, value));
	}

	@Test
	public void read_double_value_from_config_JSON_Key_has_non_numeric_value_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		double defaultValue = 7.13f;
		double value = Config.getDouble("testkey", defaultValue);
		assertTrue(doublesEquals(defaultValue, value));
	}

	@Test
	public void read_double_value_from_config_JSON_NON_EXISTING_KEY_returns_default_Test(){
		Config.setConfigSourceJSON(configJsonFilePath);
		double defaultValue = 7.13f;
		double value = Config.getDouble("non.existing key", defaultValue);
		assertTrue(doublesEquals(defaultValue, value));
	}

	
	
}
