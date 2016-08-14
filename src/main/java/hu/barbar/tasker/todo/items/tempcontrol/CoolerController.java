package hu.barbar.tasker.todo.items.tempcontrol;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import hu.barbar.tasker.util.OutputConfig;

/**
 * Class to control aquarium cooler. 
 * 
 * <br>Can use for I/O outputs and
 * <br>for PWM-controlled outputs too.
 * 
 * @author Barbar
 */
public class CoolerController extends TempController {

	/**
	 * @param myOutputConfig to define output pin/channel what will be used for.
	 * @param title for toString-like methods.
	 * @param observedSensor define identifier of sensor
	 * @param ruleItems to define rules what will be used for control
	 */
	public CoolerController(OutputConfig myOutputConfig, String title, int observedSensor, ArrayList<RuleItem> ruleItems) {
		super(myOutputConfig, title, observedSensor, ruleItems);
	}
	
	public CoolerController(OutputConfig myOutputConfig, JSONObject coolerControllerJson) {
		super(myOutputConfig, coolerControllerJson);
	}
	
	
	@Override
	protected boolean isValueExceedLimit(float value, float limit) {
		// In CoolerController "exceeds" means value is higher than limit..
		return (value > limit);
	}

	@Override
	public String getClassTitle() {
		return "Temperature based cooler controller";
	}

	@Override
	public String getClassName() {
		return "CoolerController";
	}

	@Override
	protected int getType() {
		return Type.COOLER;
	}
	
}
