package hu.barbar.tasker.todo.items;

import java.text.SimpleDateFormat;
import java.util.Date;

import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Defaults;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

public class TempLogger2 extends TempRelatedToDoItemBase {
	
	private String temperatureLogFile = null;
	
	private SimpleDateFormat sdf = null;
	
	
	
	public TempLogger2() {
		super();
		
		// Get this parameter from config JSON..
		String tempLogPath = FileHandler.guaranteePathSeparatorAtEndOf( Config.get("web ui.path for log folder", Defaults.PATH_FOR_LOG_FOLDER) );
		this.temperatureLogFile = tempLogPath + Config.get("temp logger.filename of basic temperature log", Defaults.FILENAME_OF_BASIC_TEMP_LOG_FILE);
		
		sdf = new SimpleDateFormat(Config.get("temp logger.datetime parrern for temp log lines", Defaults.DATETIME_PATTERN_OF_TEMPLOG_LINES));
		
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature logger";
	}

	@Override
	public void execute() {
		//Sample: 
		// "2016-07-12 12:29, 26.76, 25.01\n" +
		String line = "\"" + sdf.format(new Date());
		
		float airTemp = readTemperature().getTempOfAir();
		float waterTemp = readTemperature().getTempOfWater();
		
		if( TemperatureResult.isValueInvalid(airTemp) ||
			TemperatureResult.isValueInvalid(waterTemp) ){
			Log.w("TempLogger2 (ToDoItem: "
					+ getId()
					+ ") Invalid temp value has been read: air: " + airTemp  + " water: " + waterTemp);
			return;
		}
		
		line += ", " +
				String.format("%.2f", airTemp).replace(",", ".")   + 
				", " +
				String.format("%.2f", waterTemp).replace(",", ".") + "\\n\"";
		
		if(FileHandler.appendToFile(this.temperatureLogFile, line)){
			// Success, do nothing
		}else{
			Log.e("TempLogger2 :: Error while try to write temp log to file: " + temperatureLogFile);
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
