package hu.barbar.tasker.util;

import java.io.File;
import java.util.Map;

import hu.barbar.util.logger.Log;

public class Env {
	
	public static boolean DEBUG_NEEDED = true;
	
	private static boolean runningOnTargetDeviceCached = false;
	
	private static String pathOfWebContent = null;
	
	
	/**
	 * Name of data folder to store files of tasker application
	 * <br> Default name: <b>"taskerData"</b>
	 */
	public static final String NAME_OF_DATA_FOLDER = "taskerData";

	
	
	/**
	 * @return true if app running on target device (with linux OS)
	 */
	public static boolean runningOnTargetDevice(){
		if(runningOnTargetDeviceCached){
			return true;
		}else{
			Env.runningOnTargetDeviceCached = (System.getProperty("os.name").contains("Linux")); 
		}
		return Env.runningOnTargetDeviceCached;
	}
	
	/**
	 * @return path separator ("/" or "\") according to the used operating system.
	 */
	public static String getPathSeparator(){
		if(runningOnTargetDeviceCached){
			return "/";
		}
		return (runningOnTargetDevice()?("/"):("\\"));
	}
	
	private static String getUserHomeDir(){
		return System.getProperty("user.home");
	}
	
	/**
	 * @return path of data folder (named as value of NAME_OF_DATA_FOLDER) 
	 */
	public static String getDataFolderPath(){
		//return getUserHomeDir() + getPathSeparator() + NAME_OF_DATA_FOLDER + getPathSeparator();  //TODO: <- not works when app started with superuser rights
		return (runningOnTargetDevice()?( "/home/pi/" + NAME_OF_DATA_FOLDER + getPathSeparator() ):(getUserHomeDir() + getPathSeparator() + NAME_OF_DATA_FOLDER + getPathSeparator()));
	}
	
	/**
	 * @return path of web UI content  
	 */
	public static String getWebContentFolderPath(){
		if(pathOfWebContent == null){
			pathOfWebContent = Config.get("web ui.path of live web content", Defaults.PATH_OF_LIVE_WEB_CONTENT);
		}
		return pathOfWebContent;
	}
	
	/**
	 *  Check that the data folder with defined name (NAME_OF_DATA_FOLDER) exists or
	 *  <br> create folder if not exists.
	 */
	public static void guaranteeDataFolderExists(){
		File f = new File(getDataFolderPath());
		if(f.exists() && f.isDirectory()){
			if(DEBUG_NEEDED)
				Log.d("Directory exists");
		}else{
			if(DEBUG_NEEDED)
				Log.d("Directory NOT exists");
			f.mkdirs();
			if(DEBUG_NEEDED){
				if(f.exists() && f.isDirectory()){
					Log.i("Directory created");
				}else{
					Log.w("Directory CAN NOT BE CREATED");
				}
			}
		}
	}
	
	
	public static void d(){
		
		Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                              envName,
                              env.get(envName));
        }
		
	}
	
}
