package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javafx.util.Pair;
import junit.framework.TestSuite;

public class OutputConfigTest extends TestSuite {

	private static String configJsonFilePath = Env.fixPathSeparators("..\\sample JSONs\\baseConfig_For_Test.json");

	JSONObject json = null;

	
	@Before
	public void before(){
		//System.out.println(FileHandler.readJSON(configJsonFilePath));
		Config.setConfigSourceJSON(configJsonFilePath);
	}

	
	/*
	 *  Build line for OutputConfig.Constructor from JSONObject
	 */
	
	@Test
	public void getLineFromJson_Test(){
		JSONArray arr = Config.getJSONArray("devices.outputs", true);
		if(arr.size()<=2){
			System.out.println("For further test need at least 3 output config elements.");
		}
		assertTrue(arr.size() >= 3);
		
		JSONObject obj1 = (JSONObject) arr.get(0);
		assertEquals("some output=IO 38 reversed", OutputConfig.getLineFrom(obj1));
		
		JSONObject obj2 = (JSONObject) arr.get(1);
		assertEquals("cooler=PWM 3", OutputConfig.getLineFrom(obj2));
		
		JSONObject obj3 = (JSONObject) arr.get(2);
		assertEquals("some other output=IO 37", OutputConfig.getLineFrom(obj3));
	}
	
	@Test
	public void getLineFromJson_Missing_Mandatory_Fields_Test(){
		JSONArray arr = Config.getJSONArray("devices.outputs with missing fields", true);
		if(arr.size()<=2){
			System.out.println("For further test need at least 3 output config elements.");
		}
		assertTrue(arr.size() >= 3);
		
		//Log.init(null, null, Log.Level.DEBUG, Log.Level.ERROR);
		
		JSONObject obj1 = (JSONObject) arr.get(0);
		assertEquals(null, OutputConfig.getLineFrom(obj1));
		
		JSONObject obj2 = (JSONObject) arr.get(1);
		assertEquals(null, OutputConfig.getLineFrom(obj2));
		
		JSONObject obj3 = (JSONObject) arr.get(2);
		assertEquals(null, OutputConfig.getLineFrom(obj3));
	}
	
	@Test
	public void getLineFromJson_EMPTY_array_Test(){
		JSONArray arr = Config.getJSONArray("devices.an empty array", true);
		assertTrue(arr.size() == 0);
	}
	
	@Test
	public void getLineFromJson_NULL_input_Test(){
		assertEquals(null, OutputConfig.getLineFrom(null));
	}
	
	@Test
	public void createInstaceFromJson_Test(){
		JSONArray arr = Config.getJSONArray("devices.outputs", true);
		if(arr.size()<=2){
			System.out.println("For further test need at least 3 output config elements.");
		}
		assertTrue(arr.size() >= 3);
		
		JSONObject obj = (JSONObject) arr.get(0);
		Pair<String, OutputConfig> pair = OutputConfig.createInstaceFromJson(obj);
		assertEquals("some output", pair.getKey());
		assertEquals(38, pair.getValue().getPin());
		assertEquals(OutputConfig.Type.IO, pair.getValue().getType());
		assertEquals(true, pair.getValue().isReversed());
		
		obj = (JSONObject) arr.get(1);
		pair = OutputConfig.createInstaceFromJson(obj);
		assertEquals("cooler", pair.getKey());
		assertEquals(3, pair.getValue().getPin());
		assertEquals(OutputConfig.Type.PWM, pair.getValue().getType());
		assertEquals(false, pair.getValue().isReversed());
		
		obj = (JSONObject) arr.get(2);
		pair = OutputConfig.createInstaceFromJson(obj);
		assertEquals("some other output", pair.getKey());
		assertEquals(37, pair.getValue().getPin());
		assertEquals(OutputConfig.Type.IO, pair.getValue().getType());
		assertEquals(false, pair.getValue().isReversed());
	}
	
}
