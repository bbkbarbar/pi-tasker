package hu.barbar.tasker.todo.items;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import hu.barbar.tasker.TaskExecutor;
import hu.barbar.tasker.Tasker;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Env;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.tasker.util.usagelog.UsageLog;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

//TODO: Later: read template only on start (maybe and when enable) to avoid useless repeating file read.

public class WebUIUpdater extends TempRelatedToDoItemBase {

	private static final boolean DEBUG_WEB = false;

	//private static final String DEFAULT_FILENAME_OF_TEMP_LOG_FILE = "temp.log";

	private static final String DEFAULT_FILENAME_OF_GENERATING_TIME_LOG = "webui_generation_time.log";
	
	private static final String PART_COOLER_ACTIVE = "<img src=\"res/cooler_active.png\" alt=\"Cooler active\" style=\"width:64px;height:64px;\">";
	private static final String PART_COOLER_NOT_ACTIVE = "<img src=\"res/cooler_NOT_active.png\" alt=\"Cooler not active\" style=\"width:64px;height:64px;\">";
	private static final String PART_HEATER_ACTIVE = "<img src=\"res/heater_active.png\" alt=\"Heater active\" style=\"width:64px;height:64px;\">";
	private static final String PART_HEATER_NOT_ACTIVE = "<img src=\"res/heater_NOT_active.png\" alt=\"Heater not active\" style=\"width:64px;height:64px;\">";
	
	private SimpleDateFormat sdf = null;
	
	private static String dateTimeFormatPattern = "yyyy.MM.dd HH:mm:ss";
	private static boolean useDefaultDateFormat = true;
	
	private String temperatureLogFile = null;
	
	private String generationTimeLogFile = null;
	
	private UsageLog heaterUsageLog = null;
	
	private OutputConfig outputConfigOfCooler = null;
	private OutputConfig outputConfigOfHeater = null;
	
	
	public WebUIUpdater() {
		super();
		
		/*
		 *  Read date-time format pattern to show "last update" -info..
		 */
		if(WebUIUpdater.useDefaultDateFormat){
			HashMap<String, String> conf = Config.readBaseConfig();
			String patternFromConfig = conf.get(Config.KEY_FORMAT_WEBUI_DATE_TIME_FORMAT);
			if(patternFromConfig != null && !(patternFromConfig.trim().equals("")) ){
				WebUIUpdater.dateTimeFormatPattern = patternFromConfig;
			}
			WebUIUpdater.useDefaultDateFormat = false;
		}
		
		sdf = new SimpleDateFormat(WebUIUpdater.dateTimeFormatPattern);
		
		
		String tempHistoryFeed = "";
		//TODO read config from cache..
		//Config.getConfig("");
		HashMap<String, String> conf = Config.readBaseConfig();
		if(conf.containsKey(Config.KEY_FILENAME_FEED_WEB_UI)){
			tempHistoryFeed = conf.get(Config.KEY_FILENAME_FEED_WEB_UI);
		}else{
			tempHistoryFeed = DEFAULT_FILENAME_OF_GENERATING_TIME_LOG;
		}
		
		/*
		 *  Read path of temperature log file..
		 */
		HashMap<String, String> config = Config.readBaseConfig();
		String tempLogPath = config.get(Config.KEY_PATH_OF_LOG_FOLDER);
		if(tempLogPath.charAt(tempLogPath.length()-1) != Env.getPathSeparator().charAt(0)){
			tempLogPath += Env.getPathSeparator();
		}
		this.temperatureLogFile = tempLogPath + tempHistoryFeed;
		
		
		/*
		 *  Read path of generating log file..
		 */
		String generationTimeLogPathFromConfig = config.get(Config.KEY_PATH_OF_LOG_FOLDER);
		if(generationTimeLogPathFromConfig.charAt(generationTimeLogPathFromConfig.length()-1) != Env.getPathSeparator().charAt(0)){
			tempLogPath += Env.getPathSeparator();
		}
		this.generationTimeLogFile = tempLogPath + DEFAULT_FILENAME_OF_GENERATING_TIME_LOG;
		
		/*
		 *  Get output config where cooler and heater is attached.
		 *  It needed to check state of cooler's output.
		 */
		outputConfigOfCooler = Config.getOutputConfig(Config.KEY_OUTPUT_OF_COOLER);
		outputConfigOfHeater = Config.getOutputConfig(Config.KEY_OUTPUT_OF_HEATER);
		
	}
	
	@Override
	public String getClassTitle() {
		return "WebUI updater";
	}
	
	@Override
	public void execute() {
		
		long startTime = System.currentTimeMillis();
		
		/*
		 *  Read temp 
		 */
		TemperatureResult t = readTemperature();
		String airTempToShow = null;
		if( TemperatureResult.isValueValid(t.getTempOfAir()) ){
			airTempToShow = String.format("%.2f", t.getTempOfAir());
		}else{
			airTempToShow = "??.??";
		}
		
		if(airTempToShow == null){
			return;
		}
		String waterTempToShow = "--.--";
		
		if(t.hasMultipleValues()){
			if( TemperatureResult.isValueValid(t.getTempOfWater()) ){
				waterTempToShow = String.format("%.2f", t.getTempOfWater());
			}else{
				waterTempToShow = "??.??";
			}
		}
		
		/*
		 *  System temp to show
		 */
		String systemTempToShow = "--.-";
		String[] systemTempParts = TaskExecutor.readCPUTemp().split(" ");
		if(systemTempParts.length >= 2){
			systemTempToShow = systemTempParts[2];
		}
		
		/*
		 *  Heater usage line
		 */
		String heaterUsageLine = "";
		if(this.heaterUsageLog != null){
			heaterUsageLine = "<h3 style=\"color: #008AB8;\">"
					          //+ "<strong>" 
		                      + "Heater's energy consumption: <br>"
							  + heaterUsageLog.getEnergyConsumptionInfo().toString(true) 
		                      //+ "</strong>"
		                      + "</h3>";
		}
		

		/*
		 *  Generate html from parts..
		 */
		
		ArrayList<String> htmlResult = readPart("t71-/0.html");
		
		ArrayList<String> tempLines = readPart("t71-/1_replace_temp3.html");
		for(int i=0; i<tempLines.size(); i++){
			if( tempLines.get(i).contains("Air") ){
				String line = tempLines.get(i);
				tempLines.set(i, line.replace("[X].[Y]", airTempToShow));
			}
			else
			if( tempLines.get(i).contains("Water") ){
				String line = tempLines.get(i);
				tempLines.set(i, line.replace("[X].[Y]", waterTempToShow));
			}
			else 
			if( tempLines.get(i).contains("System") ){
				String line = tempLines.get(i);
				tempLines.set(i, line.replace("[X].[Y]", systemTempToShow));
				tempLines.add(heaterUsageLine);
			}
		}
		htmlResult.addAll(tempLines);
		

		
		//tempLines = readPart("2.html");
		tempLines = readPart("t71-/2_begin.html");
		htmlResult.addAll(tempLines);
		
		tempLines = new ArrayList<String>();
		if(outputConfigOfCooler == null){ 	// It was unable to read outputConfig of cooler.. 
			tempLines.add(PART_COOLER_NOT_ACTIVE);
		}else{
			if(Tasker.getOutputState(outputConfigOfCooler) > 0){
				tempLines.add(PART_COOLER_ACTIVE);
			}else{
				tempLines.add(PART_COOLER_NOT_ACTIVE);
			}
		}
		if(outputConfigOfHeater == null){ 	// It was unable to read outputConfig of cooler.. 
			tempLines.add(PART_HEATER_NOT_ACTIVE);
		}else{
			if(Tasker.getOutputState(outputConfigOfHeater) > 0){
				tempLines.add(PART_HEATER_ACTIVE);
			}else{
				tempLines.add(PART_HEATER_NOT_ACTIVE);
			}
		}
		htmlResult.addAll(tempLines);
		
		tempLines = readPart("t71-/2_end.html");
		htmlResult.addAll(tempLines);
		
		
		if(DEBUG_WEB){
			tempLines = readPart("t71-/4_sample_diagram_content.html");
			htmlResult.addAll(tempLines);
		}else{
			
			tempLines = FileHandler.readLines(temperatureLogFile, true);
			String line;
			for(int i=0; i<tempLines.size(); i++){
				line = tempLines.get(i);
				if( i < (tempLines.size()-1) ){
					line += " +";
				}
				htmlResult.add(line);
			}
			
		}
		
		
		tempLines = readPart("t71-/5.html");
		htmlResult.addAll(tempLines);
		
		tempLines = readPart("t71-/6_replace_update.html");
		for(int i=0; i<tempLines.size(); i++){
			if( tempLines.get(i).contains("[dateTimeOfUpdate]") ){
				String line = tempLines.get(i);
				Date now = new Date();
				tempLines.set(i, line.replace("[dateTimeOfUpdate]", sdf.format(now)));
			}
		}
		htmlResult.addAll(tempLines);
		
		tempLines = readPart("t71-/7.html");
		htmlResult.addAll(tempLines);
		
		
		/*
		 *  Read base file
		 */
		//ArrayList<String> templateLines = FileHandler.readLines(Env.getDataFolderPath() + "webTemplate/index.html");
		
		
		/*
		 *  Replace values in template
		 */
		/*
		for(int i=0; i<templateLines.size(); i++){
			
			// Replace the current temperature values in template
			if( templateLines.get(i).contains("Temp") ){
				String line = templateLines.get(i);
				templateLines.set(i, line.replace("[X].[Y]", tempToShow));
			}
			
			// Replace the last update timestamp in template
			if( templateLines.get(i).contains("[dateTimeOfUpdate]") ){
				String line = templateLines.get(i);
				Date now = new Date();
				templateLines.set(i, line.replace("[dateTimeOfUpdate]", sdf.format(now)));
			}
			
		}
		/**/
		
		/*
		 *  Write current web content to appropirate folder 
		 */
		if( FileHandler.writeToFile(Env.getWebContentFolderPath() + "index.html", htmlResult) ){
			//Do nothing
		}else{
			Log.e("WebUIUpdater :: Problem occurs while try to write updated WebUI to file");
		}

		
		/*
		 *  Log elapsed time  
		 */
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		String line2Log = elapsedTime + "ms ";
		if(FileHandler.appendToFile(this.generationTimeLogFile, line2Log)){
			// Success, do nothing
		}else{
			Log.e("WebUIUpdater :: Error while try to write temp log to file: " + generationTimeLogFile);
		}
		
		
	}
	
	private ArrayList<String> readPart(String filename){
		return FileHandler.readLines(Env.getDataFolderPath() + "webTemplate/" + filename);
	}
	
	@Override
	public boolean needToRun() {
		return true;
	}

	@Override
	public String getClassName() {
		return "WebUIUpdater";
	}

	public void setHeaterUsageLog(UsageLog usageLog) {
		this.heaterUsageLog = usageLog;
	}
	
}
