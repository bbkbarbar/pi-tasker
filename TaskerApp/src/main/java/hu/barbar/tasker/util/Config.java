package hu.barbar.tasker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hu.barbar.util.FileHandler;
import hu.barbar.util.TaskerFilehandler;
import hu.barbar.util.logger.Log;

public class Config {


	public static final boolean READ_OUTPUT_CONFIGS_FROM_JSON = false;


	/**
	 *   FILES
	 */

	public static final String FILENAME_MAIL_CONFIG = "mail.conf";

	public static final String FILENAME_BASE_CONFIG = "base.conf";

	public static final String FILENAME_PINOUT_CONFIG = "pinout.conf";

	public static final String FILENAME_PINOUT_CONFIG_JSON = "outputConfig.json";



	/**
	 *   KEYS
	 */

	public static final String KEY_MAIL_SENDER_ACCOUNT = "sender";
	public static final String KEY_MAIL_SENDER_P = "sp";

	//public static final String KEY_FORMAT_WEBUI_DATE_TIME_FORMAT = "web ui datetime format pattern";

	//public static final String KEY_PATH_OF_LOG_FOLDER = "log folder path";


	public static final String KEY_OUTPUT_OF_COOLER = "output of cooler";

	public static final String KEY_OUTPUT_OF_HEATER = "output of heater";

	public static final String KEY_OUTPUT_OF_AIR_PUMP = "output of air pump";

	//public static final String KEY_INPUT_PIN_HUMIDITY = "input pin of humidity sensor";

	//public static final String KEY_FILENAME_TEMP_DATA_LOG = "temperature data log file";

	//public static final Object KEY_FILENAME_GENERATION_TIME_LOG = "web ui generation time log file";

	//public static final Object KEY_FILENAME_IO_LOG = "io log file";

	//public static final Object KEY_FILENAME_EVENT_LOG = "event log file";

	//public static final Object KEY_FILENAME_FEED_WEB_UI = "webui temp history feed";

	//public static final Object KEY_FAN_START_BOOST_TIME_IN_MS = "fan start boost time in ms";

	//public static final Object KEY_MIN_COOLER_ALONE_START_VALUE = "min cooler alone start percentage";
	
	public static class JsonKeys {
		public static final String DEVICE_FAN_START_BOST_TIME_IN_MS = "devices.fan.start boost time in ms";
		public static final String DEVICE_FAN_MIN_COOLER_START_VALUE = "devices.fan.minimum start value";
	}


	private static String configSourceJSON = null;

	private static JSONObject configJson = null;

	private static boolean configJsonHasBeenRead = false;


	public static HashMap<String, OutputConfig> outputConfigs = null;




	public static HashMap<String, String> readBaseConfig(String configFile){
		HashMap<String, String> configResult = null;

		configResult = FileHandler.readConfig(configFile);

		if(configResult == null){
			Log.e("Missing config file:\n" + configFile);
		}

		return configResult;
	}

	public static HashMap<String, String> readBaseConfig(){
		return readBaseConfig(Env.getDataFolderPath() + Config.FILENAME_BASE_CONFIG);
	}

	public static OutputConfig getOutputConfig(String key){

		if(Config.outputConfigs == null){
			if(readOutputConfigFromIni(true) == null){
				Log.e("Failed to get outputConfig for key: " + key);
				return null;
			}
		}

		OutputConfig response = Config.outputConfigs.get(key);
		if(response == null){
			Log.w("Can not find outputConfig for key: " + key);
		}

		return response;
	}


	public static HashMap<String, OutputConfig> readOutputConfig(){
		if(READ_OUTPUT_CONFIGS_FROM_JSON){
			return readOutputConfigFromJSON(false);
		}else{
			return readOutputConfigFromIni();
		}
	}

	public static HashMap<String, OutputConfig> readOutputConfig(boolean forceUpdateConfig){
		if(READ_OUTPUT_CONFIGS_FROM_JSON){
			return readOutputConfigFromJSON(false);
		}else{
			return readOutputConfigFromIni(forceUpdateConfig);
		}
	}


	public static HashMap<String, OutputConfig> readOutputConfigFromIni(){
		return readOutputConfigFromIni(false);
	}

	public static HashMap<String, OutputConfig> readOutputConfigFromIni(boolean forceUpdateConfig){

		if(forceUpdateConfig || Config.outputConfigs == null || Config.outputConfigs.size() == 0){
			Config.outputConfigs = TaskerFilehandler.readOutputConfig(Env.getDataFolderPath() + Config.FILENAME_PINOUT_CONFIG);
			if(forceUpdateConfig){
				Log.a("Forced re-read pinout config from file.");
			}else{
				Log.a("Read pinout config from file.");
			}
		}else{
			Log.a("Read pinout config from cache.");
		}

		return Config.outputConfigs;

	}


	@SuppressWarnings("unchecked")
	public static boolean storeOutputConfig(HashMap<String, OutputConfig> data){

		List<String> list = new ArrayList<String>(data.keySet());

		System.out.println("Keys:\n");
		for(int i=0; i<list.size(); i++){
			System.out.println(list.get(i));
		}

		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();
		for(int i=0; i<list.size(); i++){
			array.add( data.get(list.get(i)).getAsJsonObject(list.get(i)) );
		}
		json.put("output config", array);

		return FileHandler.storeJSON(
							Env.getDataFolderPath() + Config.FILENAME_PINOUT_CONFIG_JSON,
							json
		);

	}

	public static HashMap<String, OutputConfig> readOutputConfigJSON(String filename){
		HashMap<String, OutputConfig> map = new HashMap<String, OutputConfig>();

		JSONObject json = FileHandler.readJSON(filename);
		if(json.containsKey("output config")){
			JSONArray array = (JSONArray) json.get("output config");
			for(int i=0; i<array.size(); i++){
				JSONObject jsonItem = (JSONObject) array.get(i);

				if(jsonItem.containsKey("name")){
					String name = (String)jsonItem.get("name");
					OutputConfig oc = new OutputConfig(jsonItem);
					map.put(name, oc);
					Log.d("Output config loaded from JSON:\n \"" + name + "\" > " + oc.toString());
				}

			}
		}

		return map;
	}

	public static HashMap<String, OutputConfig> readOutputConfigFromJSON(boolean forceUpdateConfig){

		if(forceUpdateConfig || Config.outputConfigs == null || Config.outputConfigs.size() == 0){
			Config.outputConfigs = Config.readOutputConfigJSON(Env.getDataFolderPath() + Config.FILENAME_PINOUT_CONFIG_JSON);
			if(forceUpdateConfig){
				Log.d("Forced re-read pinout config from JSON file.");
			}else{
				Log.d("Read pinout config from JSON file.");
			}
		}else{
			Log.d("Read pinout config from cache.");
		}

		return Config.outputConfigs;

	}

	

	/**
	 * Get parameters from config JSON without default value.
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey The path of wanted config value (e.g.: loglevels.stdout)
	 * @param forceReadFileAgain If this is true, than it will always read JSON file again before find specified value <br>
	 * otherwise it will read it first, and will use that data for all getConfig calls later...
	 * @return an Object from JSON <br>
	 * or NULL if could not found the specified key.
	 */
	public static Object getWithoutDefault(String jsonKey, boolean forceReadFileAgain) {
		if((!configJsonHasBeenRead) || forceReadFileAgain){
			configJson = FileHandler.readJSON(configSourceJSON);
			if(configJson != null){
				configJsonHasBeenRead = true;
			}else{
				Log.e("Config: Can not read config JSON: " + configSourceJSON);
			}
		}
		return getElementFromJson(jsonKey, configJson);
	}
	
	
	/**
	 * Get parameters from config JSON with specified default value
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey The path of wanted config value (e.g.: loglevels.stdout)
	 * @param defaultValue to define the return value if specified key could not be found.
	 * @param forceReadFileAgain If this is true, than it will always read JSON file again before find specified value <br>
	 * otherwise it will read it first, and will use that data for all getConfig calls later...
	 * @return an Object from JSON <br>
	 * or with the specified default value if could not found the specified key.
	 */
	public static Object get(String jsonKey, Object defaultValue, boolean forceReadFileAgain) {
		Object result = getWithoutDefault(jsonKey, forceReadFileAgain);
		if(result == null){
			Log.d("Config: Can not find JSON key: \"" + jsonKey + "\". Use default value: " + defaultValue);
			return defaultValue;
		}else{
			return result;
		}
	}
	
	
	/**
	 * Get parameters from config JSON without default value.
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey where the value should be (e.g.: loglevels.stdout)
	 * <br><b>Note:</b> ForceReadFileAgain is disabled: It will read it first, and will use that data for all getConfig calls later...
	 * @return an Object from JSON <br>
	 * or NULL if it could not be find.
	 */
	public static Object getWithoutDefault(String jsonKey) {
		return getWithoutDefault(jsonKey, false);
	}
	
	/**
	 * Get parameters from config JSON without default value.
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey where the value should be (e.g.: loglevels.stdout)
	 * <br><b>Note:</b> ForceReadFileAgain is disabled: It will read it first, and will use that data for all getConfig calls later...
	 * @return an String from JSON <br>
	 * or NULL if it could not be find.
	 */
	public static String getStringWithoutDefault(String jsonKey) {
		return (String)(getWithoutDefault(jsonKey));
	}

	
	/**
	 * Get parameters from config JSON
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey where the value should be (e.g.: loglevels.stdout)
	 * <br><b>Note:</b> ForceReadFileAgain is disabled: It will read it first, and will use that data for all getConfig calls later...
	 * @param defaultValue to define the return value if specified key could not be found.
	 * @return an Object from JSON <br>
	 * or NULL if it could not be find.
	 */
	public static Object get(String jsonKey, Object defaultValue) {
		return get(jsonKey, defaultValue, false);
	}


	/**
	 * Get parameters from config JSON
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey where the value should be (e.g.: loglevels.stdout)
	 * <br><b>Note:</b> ForceReadFileAgain is disabled: It will read it first, and will use that data for all getConfig calls later...
	 * @param defaultValue to define the return value if specified key could not be found.
	 * @return a String from JSON <br>
	 * or NULL if it could not be find.
	 */
	public static String get(String jsonKey, String defaultValue) {
		return get(jsonKey, defaultValue, false);
	}
	
	
	/**
	 * Get parameters from config JSON with specified default value
	 * <br> Note: Need to call {@link #setConfigSourceJSON(String) setConfigSourceJSON} method once before using..
	 * @param jsonKey The path of wanted config value (e.g.: loglevels.stdout)
	 * @param defaultValue to define the return value if specified key could not be found. 
	 * @param forceReadFileAgain If this is true, than it will always read JSON file again before find specified value <br>
	 * otherwise it will read it first, and will use that data for all getConfig calls later...
	 * @return an String object from JSON <br>
	 * or with the specified default value if could not found the specified key.
	 */
	public static String get(String jsonKey, String defaultValue, boolean forceReadFileAgain) {
		String result = (String) getWithoutDefault(jsonKey, forceReadFileAgain);
		if(result == null){
			return defaultValue;
		}else{
			return result;
		}
	}
	
	/**
	 * Get Integer parameters from config JSON with specified default value
	 * @param jsonKey The path of wanted config value (e.g.: loglevels.stdout)
	 * @param defaultValue to define the return value if specified key could not be found. 
	 * @return an integer value what found in JSON under specified key-path <or>
	 * with the defined default value
	 */
	public static int getInt(String jsonKey, int defaultValue) {
		
		if(jsonKey == null){
			return defaultValue;
		}
		
		Object foundObject = getWithoutDefault(jsonKey);
		if(foundObject == null){
			return defaultValue;
		}
		
		try{
			Integer value = Integer.valueOf( (foundObject + "" ) );
			return value;
		}catch (NumberFormatException nfe) {
			return defaultValue;
		}
		
	}
	

	

	/**
	 * Find the value for a specified key in given JSON object recursively.
	 * @param jsonKey
	 * @param json
	 * @return the value of specified key if it exists
	 * <br> or returns NULL otherwise
	 */
	private static Object getElementFromJson(String jsonKey, JSONObject json){

		if(json == null){
			Log.e("Could not read find key in JSON: \"" + jsonKey + "\"; JSON object is null!");
			return null;
		}

		if(json.containsKey(jsonKey)){
			return json.get(jsonKey);
		}

		if(jsonKey.contains(".")){

			//System.out.println("Key |" + jsonKey + "| contains \".\"");

			String firstPartOfKey = jsonKey.substring(0, jsonKey.indexOf("."));
			String newKey = jsonKey.substring(jsonKey.indexOf(".") + 1);
			//System.out.println("firstPartOfKey: " + firstPartOfKey);
			//System.out.println("newKey: |" + newKey + "|");
			JSONObject newJSONObject = (JSONObject) json.get(firstPartOfKey);
			//System.out.println("NewJsonObject:\n" + newJSONObject);
			return getElementFromJson(newKey,newJSONObject);

		}

		return null;

	}

	
	/**
	 * Set the filepath of configSource JSON. (e.g.: ../data/config.json)
	 * <br> This JSON file will be used to find the specified key by the getConfig.. methods.
	 * @param configSourceFilePath
	 */
	public static void setConfigSourceJSON(String configSourceFilePath) {
		Config.configSourceJSON = configSourceFilePath;
		if(!FileHandler.fileExists(configSourceFilePath)){
			Log.e("Setted config source JSON ("
					+ configSourceFilePath
					+ ")is not exists!");
		}else{
			Log.i("Config source is set from JSON: " + configSourceFilePath);
		}
	}


}
