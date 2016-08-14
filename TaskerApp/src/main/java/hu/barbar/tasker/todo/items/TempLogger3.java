package hu.barbar.tasker.todo.items;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import hu.barbar.tasker.Tasker;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Env;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.FileHandler;
import hu.barbar.util.logger.Log;

public class TempLogger3 extends TempRelatedToDoItemBase {

	private static final String PATTERN_OF_TEMPLOG_LINES = "yyyy-MM-dd HH:mm";

	private String temperatureLogFile = null;
	
	private String DEFAULT_FILENAME_OF_TEMP_LOG_FILE = "temp_and_cooler.log";
	
	private SimpleDateFormat sdf = null;
	
	private OutputConfig outputConfigOfCooler = null;
	
	
	public TempLogger3(OutputConfig outputConfigOfCooler) {
		super();
		
		this.outputConfigOfCooler = outputConfigOfCooler;
		
		HashMap<String, String> config = Config.readBaseConfig();
		String tempLogPath = config.get(Config.KEY_PATH_OF_LOG_FOLDER);
		if(tempLogPath.charAt(tempLogPath.length()-1) != Env.getPathSeparator().charAt(0)){
			tempLogPath += Env.getPathSeparator();
		}
		this.temperatureLogFile = tempLogPath + DEFAULT_FILENAME_OF_TEMP_LOG_FILE;
		
		sdf = new SimpleDateFormat(TempLogger3.PATTERN_OF_TEMPLOG_LINES);
		
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature logger with cooler output.";
	}

	@Override
	public void execute() {
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

}
