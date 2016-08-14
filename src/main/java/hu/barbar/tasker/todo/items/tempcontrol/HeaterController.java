package hu.barbar.tasker.todo.items.tempcontrol;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import hu.barbar.tasker.util.OutputConfig;

public class HeaterController extends TempController {

	

	public HeaterController(OutputConfig myOutputConfig, String title, int observedSensor, ArrayList<RuleItem> ruleItems) {
		super(myOutputConfig, title, observedSensor, ruleItems);
	}
	
	public HeaterController(OutputConfig myOutputConfig, JSONObject coolerControllerJson) {
		super(myOutputConfig, coolerControllerJson);
	}

	@Override
	protected boolean isValueExceedLimit(float value, float limit) {
		// In HeaterController "exceeds" means value is lower than limit..
		return (value < limit);
	}

	@Override
	public String getClassTitle() {
		return "Temperature based heater controller";
	}

	@Override
	protected int getType() {
		return Type.HEATER;
	}

	@Override
	public String getClassName() {
		return "HeaterController";
	}


}
