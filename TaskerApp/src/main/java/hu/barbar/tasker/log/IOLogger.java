package hu.barbar.tasker.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Env;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

public class IOLogger {
	
	private static final String DEFAULT_FILE_NAME = "io.log";
	
	private static final String DATE_PATTERN_OF_IOLOG_LINES = "yyyy-MM-dd HH:mm";

	private static String ioLogFile = null;
	
	private static SimpleDateFormat sdf = null;
	
	private static boolean initialized = false;
	
	
	/**
	 * Initialize IOLogger instance.
	 * @return True if successfuly initialized or <br>
	 *         False otherwise..
	 */
	public static boolean initialize() {
		
		HashMap<String, String> config = Config.readBaseConfig();
		if(config == null){
			Log.w("IOLogger can not initialized!\n" 
				+ "Can not read BaseConfig\n");
			initialized = false;
			return false;
		}
		
		String ioLogPath = Config.getStringWithoutDefault("log.path");
		if(ioLogPath == null){
			Log.w("IOLogger can not initialized!\n" 
				+ "Can not read path for IO log.\n");
			initialized = false;
			return false;
		}

		if(ioLogPath.charAt(ioLogPath.length()-1) != Env.getPathSeparator().charAt(0)){
			ioLogPath += Env.getPathSeparator();
		}
		IOLogger.ioLogFile = ioLogPath + DEFAULT_FILE_NAME; 
		
		sdf = new SimpleDateFormat(DATE_PATTERN_OF_IOLOG_LINES);

		initialized = true;
		
		Log.i("IOLogger initialized:");
		Log.i("IO log file: " + IOLogger.ioLogFile);
		Log.d("IO log date format: " + IOLogger.DATE_PATTERN_OF_IOLOG_LINES + "\n");
		
		return true;
	}
	
	public static boolean isInitialized(){
		return IOLogger.initialized;
	}
	
	public static void add(int pin, boolean state){
		
		if(IOLogger.isInitialized()){
		
			// "2016-07-12 12:29, 74\n" +
			String line = sdf.format(new Date()) + "\t";
	
			line += "pin: " + pin;
			line += " state: " + (state?"1":"0");
			
			if(FileHandler.appendToFile(IOLogger.ioLogFile, line)){
				// Success, do nothing
			}else{
				Log.e("Error while try to write io log to file: " + ioLogFile);
			}
		
		}else{
			Log.w("IOLogger is NOT initialized.");
		}
		
	}

}
