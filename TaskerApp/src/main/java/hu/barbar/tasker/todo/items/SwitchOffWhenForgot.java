package hu.barbar.tasker.todo.items;

import org.json.simple.JSONObject;

import hu.barbar.tasker.todo.items.util.TempReader;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.util.logger.Log;

/**
 *  A ToDoItem to define rules (from json file) to switch OFF peripherals <br> 
 *  when it reaches the specified temperature.  <br>
 *  It is a safety function for those case when user forgot <br> 
 *  to turn of heater or cooler what he manually turned on earlier.  
 * 
 * @author Barbar
 */
public class SwitchOffWhenForgot extends TempRelatedToDoItemBase {

	
	protected int observedSensor = TempReader.SENSOR_WATER;
	
	
	//TODO !!!!!!!!! folytani itt !!!!!!!!
	public SwitchOffWhenForgot(int observerSensor, JSONObject rule) {
		
		/*
		 *  enabled
		 */
		this.enabled = true;
		
		/*
		 *  observedSensor
		 */
		if(TempReader.validateSensorSelection(observerSensor)){
			this.observedSensor = observerSensor;
		}else{
			Log.w(this.getClassName() + ": constructor got an invalid sensorType (" + observerSensor + ")\n"
					+ "Overriden with default: SENSOR_WATER.");
		}
		
	}
	
	@Override
	public String getClassName() {
		return "SwitchOffWhenForgot";
	}

	@Override
	public String getClassTitle() {
		return "SwitchOffWhenForgot";
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean needToRun() {
		return true;
	}

}
