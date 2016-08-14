package hu.barbar.tasker.todo.items;

import hu.barbar.tasker.TaskExecutor;
import hu.barbar.util.Mailer;

public class TempCheck extends ToDoItemBase {
	
	private static final float MEASURED_VALUE_UNDEFINED = 95.000f;
	private static final float MEASURED_VALUE_NOT_CORRENT = 85.000f;
	
	private float lastMeasuredValue = MEASURED_VALUE_UNDEFINED; 
	
	private float relevantDifference = 0;
	
	
	public TempCheck(float minRelevantDifferene) {
		super();
		this.relevantDifference = minRelevantDifferene;
	}
	
	public TempCheck(String title, float minRelevantDifferene) {
		this.title = title;
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
		return "Temperature listener";
	}

	@Override
	public void execute() {
		String response = TaskExecutor.readTemp("not_used_yet");
		String responseParts[] = response.split(" ");
		String tempStr = responseParts[responseParts.length-1];
		float temperature = Float.valueOf(tempStr);
		
		if( (lastMeasuredValue != MEASURED_VALUE_UNDEFINED) && (temperature != MEASURED_VALUE_NOT_CORRENT) ){
			float dt = diff(temperature, lastMeasuredValue);
			if( dt > this.getRelevantDifference() ){
				Mailer.sendEmail(
						"bbk.barbar@gmail.com",
						"Temperature warning",
						"Last measured temperature: " + this.getLastMeasuredTemperature() + "'C\n" + 
						"Current temperature: " + temperature + "'C\n" +
						"Difference: " + dt
				);
			}
		}
		
		lastMeasuredValue = temperature;
	}

	@Override
	public boolean needToRun() {
		return true;
	}

	
	private float diff(float a, float b){
		return (a>b)?(a-b):(b-a);
	}
	
}
