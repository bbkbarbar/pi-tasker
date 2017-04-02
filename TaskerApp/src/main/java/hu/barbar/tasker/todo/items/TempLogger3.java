package hu.barbar.tasker.todo.items;

import java.text.SimpleDateFormat;
import java.util.Date;

import hu.barbar.tasker.Tasker;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Defaults;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

public class TempLogger3 extends TempRelatedToDoItemBase {

	private String temperatureLogFile = null;
	
	private SimpleDateFormat sdf = null;
	
	private OutputConfig outputConfigOfCooler = null;
	
	private boolean isInitialized = false;
	
	
	public TempLogger3(OutputConfig outputConfigOfCooler) {
		super();
		if(outputConfigOfCooler == null){
			Log.e("TempLogger3: Can not initialite temperature logger instance. OutputConfigOfCooler is null!");
			isInitialized = false;
			return;
		}
		this.outputConfigOfCooler = outputConfigOfCooler;
		// Get this parameter from config JSON..
		String tempLogPath = FileHandler.guaranteePathSeparatorAtEndOf( Config.getString("temp logger.path for log folder", Defaults.PATH_FOR_LOG_FOLDER) );
		this.temperatureLogFile = tempLogPath + Config.getString("temp logger.filename of temperature log", Defaults.FILENAME_OF_TEMP_LOG_FILE);
		
		sdf = new SimpleDateFormat(Config.getString("temp logger.datetime parrern for temp log lines", Defaults.DATETIME_PATTERN_OF_TEMPLOG_LINES));
		
		isInitialized = true;
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature logger with cooler output.";
	}

	@Override
	public void execute() {
		
		if(!this.isInitialized){
			Log.w("TempLogger3: Can not execute: Could not be initialized!");
			return;
		}
		
		//Sample: 
		// "2016-07-12 12:29, 26.76, 25.01, 26.00\n" + 	//where 25.60 means 25 + 12% (which means 0.6 in range of 25..30)
		String line = "\"" + sdf.format(new Date());
		
		float airTemp = readTemperature().getTempOfAir();
		float waterTemp = readTemperature().getTempOfWater();
		float coolerStateinRange = -1;
		if(outputConfigOfCooler.getType() == OutputConfig.Type.IO){
			if(Tasker.getOutputState(outputConfigOfCooler) > 0){
				coolerStateinRange = 30f;
			}else{
				coolerStateinRange = 25f;
			}
		}else
		if(outputConfigOfCooler.getType() == OutputConfig.Type.PWM){
			coolerStateinRange = ((Tasker.getOutputState(outputConfigOfCooler)/40.95f) * 0.05f) + 25f;
		}
		
		
		
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
				String.format("%.2f", waterTemp).replace(",", ".") + 
				", " +
				String.format("%.2f", coolerStateinRange).replace(",", ".") +
				"\\n\"";
		
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
