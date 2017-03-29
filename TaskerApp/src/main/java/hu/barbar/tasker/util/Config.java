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
	
	public static final String KEY_FORMAT_WEBUI_DATE_TIME_FORMAT = "web ui datetime format pattern";

	public static final String KEY_PATH_OF_LOG_FOLDER = "log folder path";
	
	
	public static final String KEY_OUTPUT_OF_COOLER = "output of cooler";
	
	public static final String KEY_OUTPUT_OF_HEATER = "output of heater";
	
	public static final String KEY_OUTPUT_OF_AIR_PUMP = "output of air pump";
	
	public static final String KEY_INPUT_PIN_HUMIDITY = "input pin of humidity sensor";
	
	public static final String KEY_FILENAME_TEMP_DATA_LOG = "temperature data log file";
	
	public static final Object KEY_FILENAME_GENERATION_TIME_LO = "web ui generation time log file";

	public static final Object KEY_FILENAME_IO_LOG = "io log file";
	
	public static final Object KEY_FILENAME_EVENT_LOG = "event log file";

	public static final Object KEY_FILENAME_FEED_WEB_UI = "webui temp history feed";
	
	public static final Object KEY_FAN_START_BOOST_TIME_IN_MS = "fan start boost time in ms";
	
	public static final Object KEY_MIN_COOLER_ALONE_START_VALUE = "min cooler alone start percentage";

	
	private static String configSourceJSON = null;
	
	public static HashMap<String, OutputConfig> outputConfigs = null;
	
	
	
	
	public static HashMap<String, String> readBaseConfig(String configFile){
		HashMap<String, String> configResult = null;
		
		configResult = FileHandler.readConfig(configFile);
		
		if(configResult == null){
			//TODO handle case when config file can not be read.
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
				Log.t("Forced re-read pinout config from file.");
			}else{
				Log.t("Read pinout config from file.");
			}
		}else{
			Log.t("Read pinout config from cache.");
		}
		
		return Config.outputConfigs;
		
	}
	
	
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
	
	//TODO
	public static HashMap<String, OutputConfig> readOutputConfigFromJSON(boolean forceUpdateConfig){
		
		if(forceUpdateConfig || Config.outputConfigs == null || Config.outputConfigs.size() == 0){
			Config.outputConfigs = Config.readOutputConfigJSON(Env.getDataFolderPath() + Config.FILENAME_PINOUT_CONFIG_JSON);
			if(forceUpdateConfig){
				Log.t("Forced re-read pinout config from JSON file.");
			}else{
				Log.t("Read pinout config from JSON file.");
			}
		}else{
			Log.t("Read pinout config from cache.");
		}
		
		return Config.outputConfigs;
		
	}
	

	
	/**
	 * Get parameters from config JSON
	 * @param jsonPath where the value should be
	 * @return an Object from JSON <br>
	 * or NULL if it could not be find.
	 */
	public static Object getConfig(String jsonPath) {
		
		return null;
	}
	
	

	public static void setConfigSourceJSON(String configSource) {
		Config.configSourceJSON = configSource;
		if(!FileHandler.fileExists(configSource)){
			Log.e("Setted config source JSON ("
					+ configSource 
					+ ")is not exists!");
		}else{
			Log.i("Config source is set from JSON: " + configSource);
		}
	}
	
	
}
