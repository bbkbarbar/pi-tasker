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
	
	private static boolean initailazed = false;
	
	
	public static void initialize() {
		
		HashMap<String, String> config = Config.readBaseConfig();
		String ioLogPath = config.get(Config.KEY_PATH_OF_LOG_FOLDER);
		if(ioLogPath.charAt(ioLogPath.length()-1) != Env.getPathSeparator().charAt(0)){
			ioLogPath += Env.getPathSeparator();
		}
		IOLogger.ioLogFile = ioLogPath + DEFAULT_FILE_NAME; 
		
		sdf = new SimpleDateFormat(DATE_PATTERN_OF_IOLOG_LINES);

		initailazed = true;
		
		Log.i("IOLogger initialized:");
		Log.i("IO log file: " + IOLogger.ioLogFile);
		Log.d("IO log date format: " + IOLogger.DATE_PATTERN_OF_IOLOG_LINES + "\n");
		
	}
	
	public static boolean isInitialized(){
		return IOLogger.initailazed;
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
