package hu.barbar.tasker.todo.items.tempcontrol;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hu.barbar.tasker.TaskExecutor;
import hu.barbar.tasker.todo.items.RuleItemComparator;
import hu.barbar.tasker.todo.items.util.TempReader;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.todo.items.util.ToDoItemJSONInterface;
import hu.barbar.tasker.util.JSONHelper;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.logger.Log;

public abstract class TempController extends TempRelatedToDoItemBase implements ToDoItemJSONInterface {

	public class Type {
		public static final int COOLER = -1,
								HEATER = 1,
								UNDEFINED = 0;
	}
	
	public class CanNotCreateFromJSONException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5868776570380231033L;

	}
	
	public static class RuleItem{
		
		private static final String VALUE_NUMBER_FORMAT = "%.2f";

		private static final float VALUE_UNDEFINED = -999f;

		private static final int OUTPUT_UNDEFINED = -1;

		private float limitValue;
		
		private float threshold;
		
		private int outputValueWhenExceeded;
		
		//private int lastOutputValue = -1;
		
		
		/**
		 * @param limitValue
		 * @param threshold
		 * @param outputValueWhenExceeded in percentage (0..100)
		 */
		public RuleItem(float limitValue, float threshold, int outputValueWhenExceeded){
			this.limitValue = limitValue;
			this.threshold = threshold;
			this.outputValueWhenExceeded = outputValueWhenExceeded;
		}
		
		/**
		 * Create RuleItem object from JSON object
		 * @param limitValue
		 * @param threshold
		 * @param outputValueWhenExceeded in percentage (0..100)
		 * @throws CanNotCreateFromJSONException 
		 */
		public RuleItem(JSONObject json) throws CanNotCreateFromJSONException{
			this.limitValue = RuleItem.VALUE_UNDEFINED;
			this.threshold = RuleItem.VALUE_UNDEFINED;
			this.outputValueWhenExceeded = RuleItem.OUTPUT_UNDEFINED;
			
			if(json == null){
				System.out.println("\n\nNULL\n\n");
				Log.e("RuleItem: Can not create instance because json object is NULL!");
				return;
			}
			
			if( json.containsKey("limitValue") &&
				json.containsKey("threshold")  &&
				json.containsKey("outputValueWhenExceeded") ){
				
				try {
					this.limitValue = JSONHelper.getFloat(json, "limitValue");
					this.threshold =  JSONHelper.getFloat(json, "threshold");
					this.outputValueWhenExceeded = JSONHelper.getInt(json, "outputValueWhenExceeded");
				} catch (NumberFormatException e) {
					Log.w("NumberFormatException cought while tried to create RuleItem from JSON object");
					e.printStackTrace();
					//TODO
					//throw new RuleItem.CanNotCreateFromJSONException();
				}
				
			}else{
				Log.w("Can not create RuleItem from json object.");
			}
		}
		
		public boolean hasValidValues(){
			return ( this.limitValue != RuleItem.VALUE_UNDEFINED &&
					 this.threshold != RuleItem.VALUE_UNDEFINED  &&
					 this.outputValueWhenExceeded != RuleItem.OUTPUT_UNDEFINED );
		}
		
		
		public String toString(){
			return "Limit: " + String.format(RuleItem.VALUE_NUMBER_FORMAT, limitValue) + " " +
				   "Threshold: " + String.format(RuleItem.VALUE_NUMBER_FORMAT, threshold) + " " +
				   "OutputValueWhenExceeded: " + outputValueWhenExceeded;
		}
		
		public String getLine(boolean isUsedForCooler){
			if(isUsedForCooler)
				return "\t\t[" + String.format("%.2f", this.getLimitValue()) + " - " + String.format("%.2f", (this.getLimitValue() - this.getThreshold())) + "]°C >> " + this.getOutputValue() + "%";
			else
				return "\t\t[" + String.format("%.2f", this.getLimitValue()) + " - " + String.format("%.2f", (this.getLimitValue() + this.getThreshold())) + "]°C >> " + this.getOutputValue() + "%";
		}
		
		public float getLimitValue() {
			return limitValue;
		}

		public float getThreshold() {
			return threshold;
		}

		public int getOutputValue() {
			return outputValueWhenExceeded;
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject getAsJSON(){
			JSONObject res = new JSONObject();
			res.put("limitValue", String.format("%.3f", this.limitValue));
			res.put("threshold", String.format("%.3f", this.threshold));
			res.put("outputValueWhenExceeded", this.outputValueWhenExceeded);
			return res;
		}
		
	}

	
	protected class AloneTemperatureMeasurementResultForOneSensorOnly{
		
		private float value = TEMPERATURE_VALUE_UNDEFINED;
		
		private boolean valueIsValid = false;

		public AloneTemperatureMeasurementResultForOneSensorOnly(float value, boolean hasValidValue) {
			super();
			this.value = value;
			this.valueIsValid = hasValidValue;
		}

		public float getValue() {
			return value;
		}

		public boolean hasValidValue() {
			return valueIsValid;
		}
		
	}
	
	protected int observedSensor = TempReader.SENSOR_DEFAULT;

	private boolean initialized = false;
	
	/**
	 * Type can be: <b>TempController.Type.COOLER</b>
	 * 				<br> or <br>
	 * 				<b>TempController.Type.HEATER</b>
	 * @return
	 */
	protected abstract int getType();
	
	protected OutputConfig outputConfig = null;
	
	protected ArrayList<RuleItem> ruleItems = null;
	
	
	protected static final int NONE = -1;
	
	protected int exceededLevel = NONE;

	private int lastOutputValue;

	public TempController(OutputConfig myOutputConfig, String title, int observedSensor, ArrayList<RuleItem> ruleItems) {
		super();
		
		this.initialized = true;
		this.exceededLevel = NONE;
		this.title = title;
		this.outputConfig = myOutputConfig;
		this.observedSensor = observedSensor;
		this.ruleItems = ruleItems;
		
		if(outputConfig.isInvalid()){
			if(title == null || title.trim().equals(""))
				Log.e("CoolerController can not be initialized: OutputConfig is invalid.");
			else
				Log.e("CoolerController (" + this.title + ") can not be initialized: OutputConfig is invalid.");
			this.initialized = false;
		}
		
		if(this.ruleItems == null || this.ruleItems.size() == 0){
			if(title == null || title.trim().equals("")){
				Log.e("CoolerController can not be initialized: No ruleItems!");
			}else{
				Log.e("CoolerController (" + this.title + ") can not be initialized: No ruleItems!");
			}
			this.initialized = false;
			return;
		}
		
		this.ruleItems.sort(new RuleItemComparator());
		
	}
	
	public TempController(OutputConfig myOutputConfig, JSONObject coolerControllerJson) {
		super();
		
		this.outputConfig = myOutputConfig;
		
		this.exceededLevel = NONE;
		this.initialized = true;
		
		if(coolerControllerJson.containsKey("observedSensor") && coolerControllerJson.containsKey("ruleItems")){
			
			/*
			 *  Title
			 */
			if(coolerControllerJson.containsKey("title")){
				this.title = (String) coolerControllerJson.get("title");
				if(this.title == null){
					this.title = "Untitled cooler controller";
				}
			}else{
				this.title = "Untitled cooler controller";
			}
			
			
			/*
			 *  ObservedSensor
			 */
			this.observedSensor = JSONHelper.getInt(coolerControllerJson, "observedSensor");
			
			
			/*
			 *  RuleItems
			 */
			this.ruleItems = new ArrayList<TempController.RuleItem>();
			JSONArray arr = (JSONArray) coolerControllerJson.get("ruleItems");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = arr.iterator();
			while(iterator.hasNext()){
				JSONObject ruleItemJson = iterator.next();
				try {
					RuleItem ri = new RuleItem(ruleItemJson);
					this.ruleItems.add(ri);
				} catch (CanNotCreateFromJSONException e) {
					Log.w("Can not add CoolerController.RuleItem from json.");
				}
			}
			
			
			/*
			 *  Enabled
			 */
			if(coolerControllerJson.containsKey("enabled")){
				String s = coolerControllerJson.get("enabled") + "";
				if(s == null || s.trim().equals("1") || s.trim().equalsIgnoreCase("true") ){
					this.setEnabled(true);
				}else{
					this.setEnabled(false);
				}
			}
			
		}else{
			/*
			 *  Can not find mandatory fields in JSON
			 */
			Log.e("Can not create CoolerController instance. Mandatory fields are missing from JSON");
		}
		
		

		
		if(outputConfig.isInvalid()){
			if(title == null || title.trim().equals(""))
				Log.e("CoolerController can not be initialized: OutputConfig is invalid.");
			else
				Log.e("CoolerController (" + this.title + ") can not be initialized: OutputConfig is invalid.");
			this.initialized = false;
		}
		
		if(this.ruleItems == null || this.ruleItems.size() == 0){
			if(title == null || title.trim().equals("")){
				Log.e("CoolerController can not be initialized: No ruleItems!");
			}else{
				Log.e("CoolerController (" + this.title + ") can not be initialized: No ruleItems!");
			}
			this.initialized = false;
			return;
		}
		
		this.ruleItems.sort(new RuleItemComparator());
		
	}
	

	/**
	 * Method to examine that is value exceed the limit or not (depends on direction (cooling or heating)).
	 * @param value the temperature value what will be examined.
	 * @param limit the limit value what can be exceeded
	 * @return
	 */
	protected abstract boolean isValueExceedLimit(float value, float limit);
	
	@Override
	public void setEnabled(boolean enabled) {
		
		if(enabled){
			// Reset values
			this.exceededLevel = NONE;
		}
		
		super.setEnabled(enabled);
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
	public abstract String getClassTitle();

	
	protected AloneTemperatureMeasurementResultForOneSensorOnly getCurrentTemp(){
		
		TemperatureResult temp = readTemperature();
		if(!temp.hasValue()){
			Log.w("TempBasedControl :: Unusable temperature value has been read.");
			return new AloneTemperatureMeasurementResultForOneSensorOnly(TEMPERATURE_VALUE_UNDEFINED, false);
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
			return new AloneTemperatureMeasurementResultForOneSensorOnly(currentTemp, false);
		}
		
		return new AloneTemperatureMeasurementResultForOneSensorOnly(currentTemp, true);
	}

	private float calculateRangeBorderWithType(float value, float thresholdValue){
		//(this.ruleItems.get(exceededLevel).getLimitValue() - this.ruleItems.get(exceededLevel).getThreshold())
		if(getType() == Type.HEATER){
			return (value + thresholdValue);
		}else{
			return (value - thresholdValue);
		}
	}
	
	@Override
	public void execute() {
		
		/*
		 *  Read value from observed sensor
		 */
		AloneTemperatureMeasurementResultForOneSensorOnly currentTempMeasurementResult = getCurrentTemp();
		if( !currentTempMeasurementResult.hasValidValue() ){
			return;
		}
		float currentTemp = getCurrentTemp().getValue();
		
		
		/*
		 * Case when current value is between the limit value and it's threshold
		 */
		if(exceededLevel != NONE){
			if( isValueExceedLimit(currentTemp, calculateRangeBorderWithType(this.ruleItems.get(exceededLevel).getLimitValue(), this.ruleItems.get(exceededLevel).getThreshold()))
				&& !isValueExceedLimit(currentTemp, this.ruleItems.get(exceededLevel).getLimitValue()) ){
				// There is no task to do. Current state of controlled output seems fine.
				return;
			}
			
		}
		
		//new CoolerController.RuleItem(25.75f, 0.25f, 35);
		//new CoolerController.RuleItem(26.50f, 0.25f, 80);
		//currentTemp: 26.00
		
		for(int i = (this.ruleItems.size()-1); i >= 0; i--){   // 1; 0;
			
			if( isValueExceedLimit(currentTemp, this.ruleItems.get(i).getLimitValue()) ){ //RuleItems[i] exceeded.
				if(exceededLevel == i){
					return;
				}
				exceededLevel = i; 	// 0
				Log.a(	"Temperature is too "
						+((this.getType() == TempController.Type.COOLER)?"high":"low")
						+ " (limit exceeded)."
						+ " RuleID: " + i + " limit: " + this.ruleItems.get(i).getLimitValue() + " current value: " + currentTemp);
				setOutput(this.ruleItems.get(i).getOutputValue());
				Log.a((this.outputConfig.getType()==OutputConfig.Type.PWM?"PWM output: ch":"IO output pin") + this.outputConfig.getPin() + ": " + this.ruleItems.get(i).getOutputValue() + "%");
				return;
			}
			
		}
		
		//There is no rule exceeded..
		if(exceededLevel != NONE){
			exceededLevel = NONE;
			Log.a("Turn "
					+ ((this.getType() == TempController.Type.HEATER)?"heater":"cooler")
					+ " OFF. ("
					+ currentTemp + "�C"
					+ ")");
			//show that cooler is NOT active
			//Tasker.coolerIsActive = false;
			setOutput(0);
		}
		
	}
	
	
	protected String getRuleStrings(){
		String s = "";
		for(int i=0; i<this.ruleItems.size(); i++){
			s += this.ruleItems.get(i).getLine( getType() == Type.COOLER );
			if(i < (this.ruleItems.size()-1)){
				s += "\n";
			}
		}
		return s;
	}
	
	@Override
	public String toString() {
		String exceededRule = "-";
		if(exceededLevel != NONE){
			exceededRule = Integer.toString(exceededLevel);
		}
		return super.toString() + " Limit exceeded: [" + exceededRule + "]" + (((exceededLevel != NONE)? " (" + this.ruleItems.get(exceededLevel).getLimitValue() + ")":"") + "\n" + getRuleStrings());
	}
	

	@Override
	public boolean needToRun() {
		return (this.initialized);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getAsJSON() {
		
		JSONObject json = new JSONObject();
		
		json.put("class", this.getClassName());
		
		json.put("title", this.title);
		json.put("observedSensor", this.observedSensor);
		json.put("enabled", this.isEnabled());
		
		JSONArray rulesAsJson = new JSONArray();
		for(int i=0; i<this.ruleItems.size(); i++){
			rulesAsJson.add(this.ruleItems.get(i).getAsJSON());
		}
		json.put("ruleItems", rulesAsJson);
		
		return json;
	}
	
	
	protected void setOutput(int outputValue){
		if(this.lastOutputValue == outputValue){
			return;
		}
		if(this.outputConfig.getType() == OutputConfig.Type.IO){
			boolean state = (outputValue > 0);
			TaskExecutor.setIOState(this.outputConfig.getPin(), state);
			this.lastOutputValue = outputValue;
		}
		else
		if(this.outputConfig.getType() == OutputConfig.Type.PWM){
			if( (outputValue <= 50) && (outputValue > 0)){
				TaskExecutor.setPwmOutput(this.outputConfig.getPin(), (int)(2100) );
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {}
			}
			TaskExecutor.setPwmOutput(this.outputConfig.getPin(), (int)(outputValue * 40.95f) );
			this.lastOutputValue = outputValue;
		}
			
	}
	
	public abstract String getClassName();

}
