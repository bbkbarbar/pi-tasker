package hu.barbar.tasker.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Env;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

public class EventLogger {

	private static final String DEFAULT_FILE_NAME_EVENT_LOG = "event.log";
	
	private static boolean initialized = false;
	
	public static String eventLogFile = null;
	
	private static final String DATE_PATTERN_OF_EVENTLOG_LINES = "yyyy-MM-dd HH:mm:ss";

	private static SimpleDateFormat sdf = null;
	
	
	
	/**
	 * Initialize EventLogger instance.
	 * @return True if successfuly initialized or <br>
	 *         False otherwise..
	 */
	public static boolean initialize(){
		
		HashMap<String, String> config = Config.readBaseConfig();
		if(config == null){
			Log.w("EventLogger can not initialized!\n" 
				+ "Can not read BaseConfig\n");
			initialized = false;
			return false;
		}

		String eventLogPath = FileHandler.guaranteePathSeparatorAtEndOf( Config.getStringWithoutDefault("log.path") );
		if(eventLogPath == null){
			Log.w("EventLogger can not initialized!\n" 
				+ "Can not read path for event log.\n");
			initialized = false;
			return false;
		}
		if(eventLogPath.charAt(eventLogPath.length()-1) != Env.getPathSeparator().charAt(0)){
			eventLogPath += Env.getPathSeparator();
		}
		eventLogFile = eventLogPath + DEFAULT_FILE_NAME_EVENT_LOG;
		
		
		sdf = new SimpleDateFormat(DATE_PATTERN_OF_EVENTLOG_LINES);
		
		initialized = true;
		
		Log.info("EventLogger initialized:");
		Log.info("Event log file: " + EventLogger.eventLogFile);
		Log.debug("Event log date format: " + EventLogger.DATE_PATTERN_OF_EVENTLOG_LINES + "\n");
		return true;
	}
	
	
	public static void add(String line){
		EventLogger.add(line, new Date());
	}
	
	public static void add(String line, Date date){
		
		if( !EventLogger.initialized ){
			Log.w("Can not add line:\n" + line);
			Log.w("EventLogger is NOT initialized!");
			return;
		}
		
		FileHandler.appendToFile(eventLogFile, EventLogger.getTimeStamp(date) + line);
		
	}
	
	private static String getTimeStamp(Date date){
		return (sdf.format(date) + " ");
	}
	
	
	public static boolean isInitialized(){
		return EventLogger.initialized;
	}
	
}
