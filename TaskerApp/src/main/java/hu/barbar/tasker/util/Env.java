package hu.barbar.tasker.util;

import java.io.File;
import java.util.Map;

import hu.barbar.util.logger.Log;

public class Env {
	
	static class OsName {
		public static final int NO_MOCK_USED = 0,
								LINUX = 1,
								WINDOWS = 2;
	}
	
	private static int mockOS = 0;
	
	public static boolean DEBUG_NEEDED = true;
	
	private static boolean runningOnTargetDeviceCached = false;
	
	private static String pathOfWebContent = null;
	
	
	/**
	 * Name of data folder to store files of tasker application
	 * <br> Default name: <b>"taskerData"</b>
	 */
	public static final String NAME_OF_DATA_FOLDER = "taskerData";

	
	static void setOSMock(int osID){
		if(osID == OsName.LINUX){
			Env.mockOS = OsName.LINUX;
		}else
		if(osID == OsName.WINDOWS){
			Env.mockOS = OsName.WINDOWS;
		}else{
			Env.mockOS = OsName.NO_MOCK_USED;
		}
	}
	
	
	/**
	 * @return true if app running on target device (with linux OS)
	 */
	public static boolean runningOnTargetDevice(){
		if(mockOS == OsName.LINUX){
			return true;
		}else
		if(mockOS == OsName.WINDOWS){
			return false;
		}else{

			if(runningOnTargetDeviceCached){
				return true;
			}else{
				Env.runningOnTargetDeviceCached = (System.getProperty("os.name").contains("Linux")); 
			}
			return Env.runningOnTargetDeviceCached;
			
		}
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
		//return getUserHomeDir() + getPathSeparator() + NAME_OF_DATA_FOLDER + getPathSeparator();  // <- not works when app started with superuser rights
		return (runningOnTargetDevice()?( "/home/pi/" + NAME_OF_DATA_FOLDER + getPathSeparator() ):(getUserHomeDir() + getPathSeparator() + NAME_OF_DATA_FOLDER + getPathSeparator()));
	}
	
	/**
	 * @return path of web UI content  
	 */
	public static String getWebContentFolderPath(){
		if(pathOfWebContent == null){
			pathOfWebContent = Config.getString("web ui.path of live web content", Defaults.PATH_OF_LIVE_WEB_CONTENT);
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
	
	
	/**
	 * Change separators in given path according to the current OS.
	 * <br> ("\" to "/") in linux, or ("/" to "\") in windows.
	 * @param path the original path
	 * @return the path with appropirate separators 
	 */
	public static String fixPathSeparators(String path){
		
		if(path == null){
			return null;
		}
		
		String res = "";
		if(Env.runningOnTargetDevice()){
			for(int i=0; i<path.length(); i++){
				if(path.charAt(i) == '\\'){
					res += '/';
				}else{
					res += path.charAt(i);
				}
			}
			return res;
		}else{
			for(int i=0; i<path.length(); i++){
				if(path.charAt(i) == '/'){
					res += '\\';
				}else{
					res += path.charAt(i);
				}
			}
			return res;
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
