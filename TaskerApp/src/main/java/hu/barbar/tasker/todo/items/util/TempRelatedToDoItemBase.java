package hu.barbar.tasker.todo.items.util;

import hu.barbar.tasker.TaskExecutor;
import hu.barbar.tasker.todo.items.ToDoItemBase;
import hu.barbar.tasker.util.TemperatureResult;

/**
 * Contains Temperature-related common constants and methods for ToDoItem descendants.  
 * @author Barbar
 */
public abstract class TempRelatedToDoItemBase extends ToDoItemBase{
	
	protected static final float TEMPERATURE_VALUE_UNDEFINED = 95.000f;
	protected static final float TEMPERATURE_VALUE_NOT_CORRENT = 85.000f;
	protected static final long TIME_OF_LAST_METERING_UNDEFINED = 0;
	
	protected static int retryCountOfReadingTemperature = 0;
	
	public static final int DIRECTION_INCREASING =  1,
							WARMER = 1,
							DIRECTION_DECREASING = -1,
							COLDER = -1,
							DIRECTION_UNDEFINED  =  0;
	
	public static String getDirectionAsHuman(int directionValue){
		
		if(directionValue > 0){
			return "above";
		}
		
		if(directionValue < 0){
			return "below";
		}
		
		return "undefined";
	}
	
	public static final int MAX_ATTEMPS_OF_READING_VALID_TEMPERATURE = 20;
	
	public static long DEFAULT_METERING_RESULT_VALIDITY_IN_SEC = 0;
	
	protected long meteringResultValidityInSec = DEFAULT_METERING_RESULT_VALIDITY_IN_SEC;
	
	
	private static TemperatureResult lastMeasuredTemperatureValue = null;
	
	private static long timeOfLastMetering = TIME_OF_LAST_METERING_UNDEFINED;
	
	
	public static final String directionToString(int d){
		switch (d) {
			case DIRECTION_INCREASING:
				return "Increasing";
			case DIRECTION_DECREASING:
				return "Decreasing";
			default:
				return "Undefined";
		}
	}
	
	public TempRelatedToDoItemBase(){
		super();
	}
	
	
	public void setValidityOfMeteringResult(long valueInSec){
		this.meteringResultValidityInSec = valueInSec;
	}
	
	protected TemperatureResult readTemperature(){
		
		if(meteringResultValidityInSec > 0){
			if( (System.currentTimeMillis() - timeOfLastMetering) < (1000 * meteringResultValidityInSec) ){
				return lastMeasuredTemperatureValue;
			}
		}
		
		String response = TaskExecutor.readTemp("not_used_yet");
		
		TemperatureResult temperature = new TemperatureResult(response);
		
		if( (!temperature.hasValue()) &&
		    (retryCountOfReadingTemperature < MAX_ATTEMPS_OF_READING_VALID_TEMPERATURE) ){
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			retryCountOfReadingTemperature++;
			return readTemperature();
		}
		
		TempRelatedToDoItemBase.lastMeasuredTemperatureValue = temperature;
		TempRelatedToDoItemBase.timeOfLastMetering = System.currentTimeMillis();
		retryCountOfReadingTemperature = 0;
		
		return temperature;
	}

	public abstract String getClassName();
	
}
