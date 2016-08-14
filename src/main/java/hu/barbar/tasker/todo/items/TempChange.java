package hu.barbar.tasker.todo.items;

import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.Mailer;

public class TempChange extends TempRelatedToDoItemBase {
	
	private float lastMeasuredValue = TEMPERATURE_VALUE_UNDEFINED; 
	
	private float relevantDifference = 0;
	
	
	public TempChange(float minRelevantDifferene) {
		super();
		init(minRelevantDifferene);
	}
	
	public TempChange(String title, float minRelevantDifferene) {
		super();
		this.title = title;
		init(minRelevantDifferene);
	}
	
	private void init(float minRelevantDifferene){
		this.relevantDifference = minRelevantDifferene;
	}
	

	public float getRelevantDifference(){
		return this.relevantDifference;
	}
	
	public float getLastMeasuredTemperature(){
		return this.lastMeasuredValue;
	}
	

	
	@Override
	public String getClassTitle() {
		return "Temperature change listener";
	}

	@Override
	public void execute() {
		float temperature = readTemperature().getTempOfAir();
		if( (lastMeasuredValue != TEMPERATURE_VALUE_UNDEFINED) && (TemperatureResult.isValueValid(temperature)) ){
			float dt = temperature-lastMeasuredValue;
			if( Math.abs(dt) > this.getRelevantDifference() ){
				Mailer.sendEmail(
						"bbk.barbar@gmail.com",
						"Temperature warning",
						"Last measured temperature: " + this.getLastMeasuredTemperature() + "'C\n" + 
						"Current temperature: " + temperature + "'C\n" +
						"Difference: " + String.format("%.2f", dt)
				);
			}
		}
		
		lastMeasuredValue = temperature;
	}

	@Override
	public boolean needToRun() {
		return true;
	}

}
