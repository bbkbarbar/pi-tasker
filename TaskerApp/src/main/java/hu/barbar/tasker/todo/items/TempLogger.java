package hu.barbar.tasker.todo.items;

import java.text.SimpleDateFormat;
import java.util.Date;

import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

public class TempLogger extends TempRelatedToDoItemBase {

	private String temperatureLogFile = null;
	
	private SimpleDateFormat sdf = null;
	
	protected static final String PATTERN_OF_TEMPLOG_LINES = "yyyy.MM.dd HH:mm";
	
	private String DEFAULT_FILENAME_OF_TEMP_LOG_FILE = "temp.log";
	
	
	
	public TempLogger() {
		super();
		
		// Not used since build 110
		//HashMap<String, String> config = Config.readBaseConfig();
		String tempLogPath = FileHandler.guaranteePathSeparatorAtEndOf( Config.get("temp logger.path for log folder", "") );
		this.temperatureLogFile = tempLogPath + DEFAULT_FILENAME_OF_TEMP_LOG_FILE;
		
		sdf = new SimpleDateFormat(TempLogger.PATTERN_OF_TEMPLOG_LINES);
		
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature logger";
	}

	@Override
	public void execute() {
		//Sample: 
		// "2016-07-12 12:29, 74\n" +
		String line = "\"" + sdf.format(new Date());
		line += ", ";
		
		float tempValue = readTemperature().getTempOfAir();
		if( TemperatureResult.isValueInvalid(tempValue)){
			Log.w("TempLogger (ToDoItem: "
					+ getId()
					+ ") Invalid temp value has been read: " + tempValue);
			return;
		}
		
		line += String.format("%.2f", tempValue).replace(",", ".") + "\\n\"";
		
		if(FileHandler.appendToFile(this.temperatureLogFile, line)){
			// Success, do nothing
		}else{
			Log.e("TempLogger :: Error while try to write temp log to file: " + temperatureLogFile);
		}
		
	}

	@Override
	public boolean needToRun() {
		return true;
	}


	@Override
	public String getClassName() {
		return "TempLogger";
	}

}
