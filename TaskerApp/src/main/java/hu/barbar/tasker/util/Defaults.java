package hu.barbar.tasker.util;

public class Defaults {

	public static final String PATH_FOR_LOG_FOLDER = Env.getDataFolderPath() + "logs" + Env.getPathSeparator();
	public static final String LOG_FILENAME = "tasker.log";
	
	public static final String FILENAME_OF_BASIC_TEMP_LOG_FILE = "temp.log";
	public static final String FILENAME_OF_TEMP_LOG_FILE = "temp_and_cooler.log";
	public static final String FILENAME_OF_TEMP_LOG_AND_COOLER_LOG_CSV = "temp_and_cooler.csv";
	public static final String DATETIME_PATTERN_OF_TEMPLOG_LINES = "yyyy-MM-dd HH:mm";
	public static final String DATETIME_PATTERN_OF_TEMPLOG_CSV = "yyyy-MM-dd HH:mm:ss";
	
	public static final String WEBUI_UPDATER_DATE_TIME_FORMAT_PATTERN = "yyyy.MM.dd HH:mm:ss";
	public static final String PATH_OF_LIVE_WEB_CONTENT = "/var/www/html/";

	public static final int THINGSPEAK_CHANNEL_ID_UNDEFINED = -36;
	
}
