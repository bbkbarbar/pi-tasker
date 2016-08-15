package hu.barbar.tasker.todo.items;

import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.Mailer;
import hu.barbar.util.logger.Log;

public class TempExceeds extends TempRelatedToDoItemBase {

	private float limitValue = TEMPERATURE_VALUE_UNDEFINED;
	
	private float backToNormalThreshold = 1.0f;
	
	private int direction = DIRECTION_UNDEFINED;
	
	private boolean limitExceeded = false;
	
	
	
	
	public TempExceeds(float limitValue, int direction, float backToNormalThreshold) {
		super();
		init(limitValue, direction, backToNormalThreshold);
	}
	
	public TempExceeds(String title, float limitValue, int direction, float backToNormalThreshold) {
		super();
		init(limitValue, direction, backToNormalThreshold);
		this.title = title;
	}
	
	private void init(float limitValue, int direction, float backToNormalThreshold){
		this.limitValue = limitValue;
		this.direction = ((direction < 0) ? DIRECTION_DECREASING : DIRECTION_INCREASING);
		this.limitExceeded = false;
		this.backToNormalThreshold = backToNormalThreshold;
	}
	
	
	public int getDirection(){
		return this.direction;
	}
	
	
	
	@Override
	public String getClassTitle() {
		return "Temperature exceeds listener";
	}

	@Override
	public void execute() {
		
		TemperatureResult temp = readTemperature();
		if(!temp.hasValue()){
			Log.w("TempExceeds :: Unuseable temperature value has been read.");
			return;
		}
		float currentTemp = temp.getTempOfAir();
		
		if( !TemperatureResult.isValueValid(currentTemp) ){
			Log.w("TempExceeds (ToDoItem: "
					+ getId()
					+ ") Invalid temp value has been read: " + currentTemp);
			return;
		}
		
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
		//TODO
	}
	
	private void onLimitExceeded(float currentTemp){
		Mailer.sendEmail(
				"bbk.barbar@gmail.com",
				"Temperature warning",
				"Current temperature (" + String.format("%.2f", currentTemp) + "'C) is " + (direction>0?"above":"below") + 
				" the limit value (" + String.format("%.2f", limitValue) + ")"
		);
	}

	@Override
	public boolean needToRun() {
		return true;
	}

	@Override
	public String getClassName() {
		return "TempExceeds";
	}

}
