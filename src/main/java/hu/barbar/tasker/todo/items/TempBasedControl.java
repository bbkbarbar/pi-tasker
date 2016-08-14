package hu.barbar.tasker.todo.items;

import hu.barbar.tasker.TaskExecutor;
import hu.barbar.tasker.log.EventLogger;
import hu.barbar.tasker.todo.items.util.TempReader;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.GPIOHelper;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.logger.Log;

public class TempBasedControl extends TempRelatedToDoItemBase {

	private float limitValue = TEMPERATURE_VALUE_UNDEFINED;
	
	private float backToNormalThreshold = 1.0f;
	
	private int direction = DIRECTION_UNDEFINED;
	
	private boolean limitExceeded = false;

	private int observedSensor = TempReader.SENSOR_DEFAULT;
	
	public static final int DEFAULT_OUTPUT_PIN = 40;
	
	private int controlledPin = DEFAULT_OUTPUT_PIN;
	
	private boolean stateWhenExceeded = true;

	private boolean initialized = false;
	
	
	public TempBasedControl(int sensorSelection, float limitValue, int direction, float backToNormalThreshold, int pinNum, boolean stateWhenExceed) {
		super();
		init(sensorSelection, limitValue, direction, backToNormalThreshold, pinNum, stateWhenExceed);
	}
	
	public TempBasedControl(int sensorSelection, String title, float limitValue, int direction, float backToNormalThreshold, int pinNum, boolean stateWhenExceed) {
		super();
		init(sensorSelection, limitValue, direction, backToNormalThreshold, pinNum, stateWhenExceed);
		this.title = title;
	}
	
	private void init(int sensorSelection, float limitValue, int direction, float backToNormalThreshold, int pinNum, boolean stateWhenExceeded){
		
		if( !GPIOHelper.isValidGPIOPin(pinNum) ){
			Log.e("TempBasedControl :: Invalid GPIO pin to control based on current temperature: " + pinNum);
			this.initialized = false;
			return;
		}
		
		this.limitValue = limitValue;
		this.direction = ((direction < 0) ? DIRECTION_DECREASING : DIRECTION_INCREASING);
		this.limitExceeded = false;
		this.backToNormalThreshold = backToNormalThreshold;
		this.observedSensor = sensorSelection;
		this.controlledPin = pinNum;
		this.stateWhenExceeded = stateWhenExceeded;
		this.initialized = true;
	}
	
	
	public boolean isExceededNow(){
		return this.limitExceeded;
	}
	
	
	public int getDirection(){
		return this.direction;
	}
	
	/**
	 * @return the type-id of observed sensor. <br>
	 * can be: <br><b>
	 *  - TempReader.SENSOR_AIR <br>
	 *  - TempReader.SENSOR_WATER <br></b>
	 */
	public int getObservedSensor() {
		return this.observedSensor;
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature controlled output listener";
	}

	@Override
	public void execute() {
		
		TemperatureResult temp = readTemperature();
		if(!temp.hasValue()){
			Log.w("TempBasedControl :: Unusable temperature value has been read.");
			return;
		}
		
		/*
		 *  Read value from observed sensor
		 */
		float currentTemp = TEMPERATURE_VALUE_UNDEFINED;
		
		if(this.observedSensor == TempReader.SENSOR_WATER){
			currentTemp = temp.getTempOfWater();
		}
		else{ // SENSOR_AIR is the "default" observed sensor
			currentTemp = temp.getTempOfAir();
		}
		
		if( !TemperatureResult.isValueValid(currentTemp) ){
			Log.w("TempBasedControl :: [" + this.getId() + "]: Can not read valid temperature value.");
		}
		
		
		/*
		 * Compare current value and setted limit value. 
		 */
		if(this.getDirection() == DIRECTION_INCREASING ){    
		//INCREASING
			
			if(currentTemp >= limitValue){ 
			//above limit
				if(limitExceeded){
					// do nothing
				}else{
					// Limit exceeded just now!!!!
					onLimitExceededUp(currentTemp);
					this.limitExceeded = true;
				}
			}else{ 
			// below limit
				if(limitExceeded && (currentTemp < (limitValue - backToNormalThreshold))){
					onLimitGoesDownToNormal(currentTemp);
					this.limitExceeded = false;
				}else{
					// do nothing
				}
			}
			
		}else{
		// DECREASING
			if(currentTemp <= limitValue){ 
			//below limit
				if(limitExceeded){
					// do nothing
				}else{
					// Limit exceeded just now!!!!
					onLimitExceededDown(currentTemp);
					this.limitExceeded = true;
				}
			}else{
			//above limit
				if(limitExceeded && currentTemp > (limitValue + backToNormalThreshold)){
					onLimitGoesUpToNormal(currentTemp);
					this.limitExceeded = false;
				}else{
					// do nothing
				}
			}
			
		}
		
	}
	
	private void onLimitGoesUpToNormal(float currentTemp) {
		onLimitGoesBackToNormal(currentTemp);
	}

	private void onLimitGoesDownToNormal(float currentTemp) {
		onLimitGoesBackToNormal(currentTemp);
	}

	private void onLimitExceededUp(float currentTemp){
		onLimitExceeded(currentTemp);
	}
	
	private void onLimitExceededDown(float currentTemp){
		onLimitExceeded(currentTemp);
	}
	
	private void onLimitGoesBackToNormal(float currentTemp){
		
		EventLogger.add(this.getClassTitle() + ": " + this.getTitle() + "\tTemp is below the normal.\tSet pin: " + controlledPin + " " + ((!this.stateWhenExceeded)?"1":"0"));
		TaskExecutor.setIOState(this.controlledPin, !this.stateWhenExceeded);
		
	}
	
	private void onLimitExceeded(float currentTemp){
		
		EventLogger.add(this.getClassTitle() + ": " + this.getTitle() + "\tTemp exceeds the limit.\tSet pin: " + controlledPin + " " + ((this.stateWhenExceeded)?"1":"0"));
		TaskExecutor.setIOState(this.controlledPin, this.stateWhenExceeded);
		
	}
	
	@Override
	public String toString() {
		return super.toString() + " Limit exceeded: [" + (limitExceeded?"Y":" ") + "]";
	}
	

	@Override
	public boolean needToRun() {
		return (true && this.initialized);
	}

}
