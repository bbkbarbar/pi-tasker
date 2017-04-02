package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class ConfigTest extends TestSuite {

	private static String configJsonFilePath = Env.fixPathSeparators("..\\sample JSONs\\baseConfig_For_Test.json");

	JSONObject json = null;

	
	@Before
	public void before(){
		//System.out.println(FileHandler.readJSON(configJsonFilePath));
		Config.setConfigSourceJSON(configJsonFilePath);
	}

	@Test
	public void read_NON_EXISTING_config_from_JSON_Test(){
		String jsonPathOfWantedValue = "non-existing-key";
		String value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals(null, value);
	}

	@Test
	public void read_single_level_config_value_from_config_JSON_Test(){
		String jsonPathOfWantedValue = "testkey";
		String value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals("good_value", value);
	}

	@Test
	public void read_multi_level_config_value_from_config_JSON_Test(){
		String jsonPathOfWantedValue = "log.levels.stdout";
		String value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals("info", value);

		jsonPathOfWantedValue = "log.levels.fileout";
		value = (String) Config.getWithoutDefault(jsonPathOfWantedValue);
		assertEquals("warn", value);
	}
	
	@Test
	public void read_value_from_config_JSON_where_the_key_contains_space_Test(){
		String jsonPathOfWantedValue = "an other key with space";
		String value = Config.getStringWithoutDefault(jsonPathOfWantedValue);
		assertEquals("yes, key can contains space", value);
	}

	
	/*
	 *  getInt
	 */
	
	@Test
	public void read_int_value_from_config_JSON_Test(){
		int defaultValue = 600;
		int value = Config.getInt("devices.fan.start boost time in ms", defaultValue);
		assertEquals(800, value);
	}
	
	@Test
	public void read_int_value_from_config_JSON_NON_EXISTING_KEY_returns_default_Test(){
		int defaultValue = 600;
		int value = Config.getInt("non.existing key", defaultValue);
		assertEquals(defaultValue, value);
	}
	
	@Test
	public void read_int_value_from_config_JSON_Key_is_NULL_Test(){
		int defaultValue = 601;
		int value = Config.getInt(null, defaultValue);
		assertEquals(defaultValue, value);
	}
	
	@Test
	public void read_int_value_from_config_JSON_Key_has_non_numeric_value_Test(){
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
		float defaultValue = 3.14f;
		float expected = 50.1f;
		float value = Config.getFloat("devices.heater.energy consumption in kwh", defaultValue);
		assertTrue(floatsEquals(expected, value));
	}

	@Test
	public void read_float_value_from_config_json_KEY_IS_NULL_Test(){
		float defaultValue = 3.14f;
		float value = Config.getFloat(null, defaultValue);
		assertTrue(floatsEquals(defaultValue, value));
	}
	
	@Test
	public void read_float_value_from_config_json_VALUE_LOOKS_LIKE_AN_INT_Test(){
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
		float defaultValue = 7.13f;
		float value = Config.getFloat("testkey", defaultValue);
		assertTrue(floatsEquals(defaultValue, value));
	}
	
	@Test
	public void read_float_value_from_config_JSON_NON_EXISTING_KEY_returns_default_Test(){
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
		double defaultValue = 3.14f;
		double expected = 50.1f;
		double value = Config.getDouble("devices.heater.energy consumption in kwh", defaultValue);
		assertTrue(doublesEquals(expected, value));
	}

	@Test
	public void read_double_value_from_config_json_KEY_IS_NULL_Test(){
		double defaultValue = 3.14f;
		double value = Config.getDouble(null, defaultValue);
		assertTrue(doublesEquals(defaultValue, value));
	}

	@Test
	public void read_double_value_from_config_json_VALUE_LOOKS_LIKE_AN_INT_Test(){
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
		double defaultValue = 7.13f;
		double value = Config.getDouble("testkey", defaultValue);
		assertTrue(doublesEquals(defaultValue, value));
	}

	@Test
	public void read_double_value_from_config_JSON_NON_EXISTING_KEY_returns_default_Test(){
		double defaultValue = 7.13f;
		double value = Config.getDouble("non.existing key", defaultValue);
		assertTrue(doublesEquals(defaultValue, value));
	}
	
	
	/*
	 *  getJSONArray
	 */
	
	@Test
	public void readJSONArrayFromConfigJSON_Test(){
		String jsonKeyForArray = "devices.outputs";
		JSONArray arr = Config.getJSONArray(jsonKeyForArray);
		
		System.out.println(arr);
		
		for(int i=0; i<arr.size(); i++){
			JSONObject obj = (JSONObject) arr.get(i);
			assertTrue(obj.containsKey("name"));
			assertTrue(obj.containsKey("pin") || obj.containsKey("channel"));
			assertTrue(obj.containsKey("type"));
		}
		
	}
	
	@Test
	public void readJSONArrayFromConfigJSON_NULL_Test(){
		String jsonKeyForArray = null;
		assertEquals(null, Config.getJSONArray(jsonKeyForArray));
	}
	
	@Test
	public void readJSONArrayFromConfigJSON_Key_not_found_Test(){
		String jsonKeyForArray = "non-existing.key";
		assertEquals(null, Config.getJSONArray(jsonKeyForArray));
	}
	
	@Test
	public void readJSONArrayFromConfigJSON_Empty_array_found_Test(){
		String jsonKeyForArray = "devices.an empty array";
		JSONArray emptyArray = new JSONArray();
		JSONArray result = Config.getJSONArray(jsonKeyForArray);
		assertEquals(0, result.size());
		assertEquals(emptyArray, result);
	}
	
}
