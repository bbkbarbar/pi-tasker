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

public class TempLogger4 extends TempRelatedToDoItemBase {

	private String temperatureLogFile = null;
	
	private SimpleDateFormat sdf = null;
	
	private OutputConfig outputConfigOfCooler = null;
	
	private boolean isInitialized = false;
	
	
	public TempLogger4(OutputConfig outputConfigOfCooler) {
		super();
		if(outputConfigOfCooler == null){
			Log.e("TempLogger4: Can not initialite temperature logger instance. OutputConfigOfCooler is null!");
			isInitialized = false;
			return;
		}
		this.outputConfigOfCooler = outputConfigOfCooler;
		// Get this parameter from config JSON..
		String tempLogPath = FileHandler.guaranteePathSeparatorAtEndOf( Config.getString("temp logger.path for log csv", Defaults.PATH_FOR_LOG_FOLDER) );
		this.temperatureLogFile = tempLogPath + Config.getString("temp logger.filename of temperature log csv", Defaults.FILENAME_OF_TEMP_LOG_FILE);
		
		sdf = new SimpleDateFormat(Config.getString("temp logger.datetime pattern for temp log csv", Defaults.DATETIME_PATTERN_OF_TEMPLOG_CSV));
		
		isInitialized = true;
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature logger with cooler output in %.";
	}

	@Override
	public void execute() {
		
		if(!this.isInitialized){
			Log.w("TempLogger4: Can not execute: Could not be initialized!");
			return;
		}
		
		//Sample: 
		// "2016-07-12 12:29, 26.76, 25.01, 26.00\n" + 	//where 25.60 means 25 + 12% (which means 0.6 in range of 25..30)
		String line = sdf.format(new Date());
		
		float airTemp = readTemperature().getTempOfAir();
		float waterTemp = readTemperature().getTempOfWater();
		if(waterTemp == 0.0f){
			Log.w("TempLogger4 (ToDoItem: "
					+ getId()
					+ ") Suspected temperature measurement problem (water temp equals: " + waterTemp + "°C");
		}
		int coolerStateinPct = -1;
		if(outputConfigOfCooler.getType() == OutputConfig.Type.IO){
			if(Tasker.getOutputState(outputConfigOfCooler) > 0){
				coolerStateinPct = 100;
			}else{
				coolerStateinPct = 0;
			}
		}else
		if(outputConfigOfCooler.getType() == OutputConfig.Type.PWM){
			coolerStateinPct = (int) (((Tasker.getOutputState(outputConfigOfCooler)*100) / OutputConfig.PWM_MAX_VALUE) + 0.5f);
		}
		
		
		
		if( TemperatureResult.isValueInvalid(airTemp) ||
			TemperatureResult.isValueInvalid(waterTemp) ){
			Log.w("TempLogger4 (ToDoItem: "
					+ getId()
					+ ") Invalid temp value has been read: air: " + airTemp  + " water: " + waterTemp);
			return;
		}
		
		line += ";" +
				String.format("%.2f", airTemp).replace(",", ".")   + 
				";" +
				String.format("%.2f", waterTemp).replace(",", ".") + 
				";" +
				Integer.toString(coolerStateinPct);
		
		if(FileHandler.appendToFile(this.temperatureLogFile, line)){
			// Success, do nothing
		}else{
			Log.e("TempLogger4 :: Error while try to write temp log to file: " + temperatureLogFile);
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
