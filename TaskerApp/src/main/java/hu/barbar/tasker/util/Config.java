package hu.barbar.tasker.util;

import java.util.HashMap;

import hu.barbar.util.FileHandler;
import hu.barbar.util.TaskerFilehandler;
import hu.barbar.util.logger.Log;

public class Config {

	/**
	 *   FILES
	 */
	
	public static final String FILENAME_MAIL_CONFIG = "mail.conf";

	public static final String FILENAME_BASE_CONFIG = "base.conf";
	
	public static final String FILENAME_PINOUT_CONFIG = "pinout.conf";
	
	
	
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
	
	
	public static final String KEY_FILENAME_TEMP_DATA_LOG = "temperature data log file";
	
	public static final Object KEY_FILENAME_GENERATION_TIME_LO = "web ui generation time log file";

	public static final Object KEY_FILENAME_IO_LOG = "io log file";
	
	public static final Object KEY_FILENAME_EVENT_LOG = "event log file";

	public static final Object KEY_FILENAME_FEED_WEB_UI = "webui temp history feed";
	
	public static final Object KEY_FAN_START_BOOST_TIME_IN_MS = "fan start boost time in ms";
	
	public static final Object KEY_MIN_COOLER_ALONE_START_VALUE = "min cooler alone start percentage";
	
	public static HashMap<String, OutputConfig> outputConfigs = null;
	
	
	public static HashMap<String, String> readBaseConfig(){
		HashMap<String, String> configResult = null;
		
		String configFile = Env.getDataFolderPath() + Config.FILENAME_BASE_CONFIG;
		configResult = FileHandler.readConfig(configFile);
		
		if(configResult == null){
			//TODO handle case when config file can not be read.
			Log.e("Missing config file:\n" + configFile);
		}
		
		return configResult;
		
	}
	
	public static OutputConfig getOutputConfig(String key){
		
		if(Config.outputConfigs == null){
			if(readOutputConfig(true) == null){
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
		return readOutputConfig(false);
	}
	
	public static HashMap<String, OutputConfig> readOutputConfig(boolean forceUpdateConfig){
		
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

	public static void getConfig(String string) {
		// TODO Auto-generated method stub
		
	}
	
	
}
