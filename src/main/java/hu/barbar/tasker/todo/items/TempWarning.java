package hu.barbar.tasker.todo.items;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hu.barbar.tasker.todo.items.util.TempReader;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.todo.items.util.ToDoItemJSONInterface;
import hu.barbar.tasker.util.JSONHelper;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.Mailer;
import hu.barbar.util.logger.Log;

public class TempWarning extends TempRelatedToDoItemBase implements ToDoItemJSONInterface {

	private static final String DATE_TIME_FORMAT_PATTERN = "yyyy.MM.dd HH:mm";

	private float limitValue = TEMPERATURE_VALUE_UNDEFINED;
	
	private float backToNormalThreshold = 1.0f;
	
	private int direction = DIRECTION_UNDEFINED;
	
	private boolean limitExceeded = false;

	private int observedSensor = TempReader.SENSOR_DEFAULT;
	
	
	private String defaultNotificationRecipient = "bbk.barbar@gmail.com";
	
	private ArrayList<String> recipientList = null;
	
	
	
	public TempWarning(int sensorSelection, float limitValue, int direction, float backToNormalThreshold) {
		super();
		init(sensorSelection, limitValue, direction, backToNormalThreshold);
	}
	
	public TempWarning(int sensorSelection, String title, float limitValue, int direction, float backToNormalThreshold) {
		super();
		init(sensorSelection, limitValue, direction, backToNormalThreshold);
		this.title = title;
	}
	
	
	public TempWarning(JSONObject json) {
		super();
		
		if(json == null){
			String errorMessage = "Can not create TempWarning item because JSONObject is null.";
			Log.e(errorMessage);
			throw new NullPointerException(errorMessage);
		}
		
		/*
		 * Check mandatory fields
		 */
		if( !(json.containsKey("limitValue") && json.containsKey("direction") && json.containsKey("backToNormalThreshold") && json.containsKey("observedSensor")) ){
			Log.e("Can not create TempWarning item because JSONObject not contains all mandatory fields.");
			//TODO throw excetion
			return;
		}
		
		/*
		 * Title
		 */
		if(json.containsKey("title")){
			this.title = (String) json.get("title");
		}else{
			this.title = "";
		}
		
		this.limitValue = JSONHelper.getFloat(json, "limitValue");
		this.direction = JSONHelper.getInt(json, "direction");
		this.backToNormalThreshold = JSONHelper.getFloat(json, "backToNormalThreshold");
		this.observedSensor = JSONHelper.getInt(json, "observedSensor");
		
		init(observedSensor, limitValue, direction, backToNormalThreshold);
		
		// "recipients"
		JSONArray recipientArrayFromJSON = (JSONArray) json.get("recipients");
		if(recipientArrayFromJSON.isEmpty()){
			this.recipientList = null;
		}else{
			this.recipientList = new ArrayList<String>();
			Iterator<String> iterator = recipientArrayFromJSON.iterator();
			while(iterator.hasNext()){
				String s = iterator.next();
				this.recipientList.add(s);
			}
		}
		
	}
	
	
	private void init(int sensorSelection, float limitValue, int direction, float backToNormalThreshold){
		this.limitValue = limitValue;
		this.direction = ((direction < 0) ? DIRECTION_DECREASING : DIRECTION_INCREASING);
		this.limitExceeded = false;
		this.backToNormalThreshold = backToNormalThreshold;
		this.observedSensor = sensorSelection;
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
	
	
	public void addRecipient(String email){
		if(this.recipientList == null){
			this.defaultNotificationRecipient = null;
			this.recipientList = new ArrayList<>();
		}
		
		this.recipientList.add(email);
	}
	
	
	@Override
	public String getClassTitle() {
		return "Temperature warning listener";
	}

	@Override
	public void execute() {
		
		TemperatureResult temp = readTemperature();
		if(!temp.hasValue()){
			Log.w("TempWarning :: Unuseable temperature value has been read.");
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
		
		if(TemperatureResult.isValueInvalid(currentTemp)){
			Log.w("TempWarning (todoItemId: " + this.getId() + "): Can not read valid temperature value.");
			return;
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
		//TODO
	}
	
	private void onLimitExceeded(float currentTemp){
		
		sendWarningEmails(currentTemp);
		
	}
	
	private void sendWarningEmails(float currentTemp) {
		
		if(recipientList != null && recipientList.size() > 0){
			//TODO
			
			for(int i=0; i<recipientList.size(); i++){
				String to = recipientList.get(i);
				if(to != null && !to.trim().equals("")){
					sendWarningEmailTo(to, currentTemp);
				}
			}

			return;
		}
		
		if(defaultNotificationRecipient != null){
			sendWarningEmailTo(defaultNotificationRecipient, currentTemp);
			return;
		}else{
			Log.w("TempWarning: Can not send warning email because defaultAddresse is null and list is null too.");
		}
		
	}

	private void sendWarningEmailTo(String to, float currentTemp){
		
		Mailer.sendEmail(
				to,
				(this.getObservedSensor() == TempReader.SENSOR_WATER)?("Water temperature warning"):("Air temperature warning"),
				getTimeStamp() + 
				" Current temperature (" + String.format("%.2f", currentTemp) + "'C) is " + (direction>0?"above":"below") + 
				" the limit value (" + String.format("%.2f", limitValue) + ")"
		);
	}
	
	
	private String getTimeStamp(){
		return this.getTimeStamp(new Date());
	}
	
	private String getTimeStamp(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
		return sdf.format(date);
	}
	
	
	@Override
	public String toString() {
		String recipients = "";
		if(this.recipientList == null){
			recipients = ": " + this.defaultNotificationRecipient;
		}else{
			recipients = "s: ";
			for(int i=0; i<this.recipientList.size(); i++){
				recipients += recipientList.get(i);
				if(i < (recipientList.size()-1) ){
					recipients += ", ";
				}
			}
		}
		return super.toString() + " Observed sensor: " + this.observedSensor + " Recipient" + recipients;
	}
	

	@Override
	public boolean needToRun() {
		return true;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJSON() {
		JSONObject json = new JSONObject();
		
		json.put("class", this.getClassName());
		
		json.put("title", this.title);
		json.put("limitValue", this.limitValue);
		json.put("direction", ((direction < 0) ? DIRECTION_DECREASING : DIRECTION_INCREASING));
		json.put("backToNormalThreshold", this.backToNormalThreshold);
		json.put("observedSensor", this.observedSensor);
		
		JSONArray recipientArray = new JSONArray();
		if(this.recipientList == null){
			recipientArray.add(defaultNotificationRecipient);
		}else{
			for(int i=0; i<this.recipientList.size(); i++){
				recipientArray.add(this.recipientList.get(i));
			}
		}
		json.put("recipients", recipientArray);
		
		return json;
	}

	@Override
	public String getClassName() {
		return "TempWarning";
	}

}
