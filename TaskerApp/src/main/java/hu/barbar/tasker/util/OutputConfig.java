package hu.barbar.tasker.util;

import org.json.simple.JSONObject;

import hu.barbar.util.Pair;
import hu.barbar.util.logger.Log;

/**
 * Class to store output configuration elements.
 * <br> An output configuration element contains:
 * <br> the type of that (IO or PWM output)
 * <br> the pin or channel of output (pin for IO or channel for PWM output)
 * <br> in case of IO output it can be reversed..
 * 
 * @author Andras
 */
public class OutputConfig {
	
	public static final int UNDEFINED = -1;
	
	public static final int INVALID = -2;
	
	public static final String TYPENAME_PWM = "pwm";
	
	public static final String TYPENAME_IO = "io";
	
	public static final String TYPENAME_UNDEFINED = "UNDEFINED";
	public static final String TYPENAME_INVALID = "INVALID";
	
	public static final String CONSTANT_OF_REVERSED_USEAGE = "reversed";

	public static final boolean DEFAULT_VALUE_OF_REVERSED = false;
	
	public static class Type {
		
		public static final int UNDEFINED = -1,
								INVALID = 0,
								IO = 1,
								PWM = 2;
		
		public static boolean invalid(int value){
			switch (value) {
				case UNDEFINED:
				case IO:
				case PWM:
					return false;
				default:
					return true;
			}
		}
		
		public static int getFromString(String text){
			if(text.equalsIgnoreCase(TYPENAME_IO)){
				return IO;
			}
			else
			if(text.equalsIgnoreCase(TYPENAME_PWM)){
				return PWM;
			}
			else{
				return UNDEFINED;
			}
		}

		public static String toString(int type) {
			switch (type) {
				case UNDEFINED:
					return "UNDEFINED";
				case IO:
					return TYPENAME_IO;
				case PWM:
					return TYPENAME_PWM;
					
				default:
					return TYPENAME_INVALID;
			}
		}
		
	}
	
	private int type = Type.UNDEFINED;
	
	private int pin = UNDEFINED;
	
	private boolean reversed = DEFAULT_VALUE_OF_REVERSED;
	
	
	/**
	 * Create OutputConfig object from exact parameters with default "reversed" value (false).
	 * @param type
	 * @param pin
	 */
	public OutputConfig(int type, int pin){
		init(type, pin, DEFAULT_VALUE_OF_REVERSED);
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject getAsJsonObject(String name){
		
		if(name == null || name.trim().length() == 0){
			return null;
		}
		
		JSONObject json = new JSONObject();
		
		json.put("name", name.trim());
		json.put("pin", pin);
		json.put("type", Type.toString(this.type));
		if(this.reversed){
			json.put("reversed", true);
		}
		
		return json;
	}
	
	public OutputConfig(JSONObject json){
		int p = UNDEFINED;
		int t = Type.UNDEFINED;
		boolean r = false;
		
		Log.d("Build OutputConfig from: " + json);
		
		if(json.containsKey("pin")){
			try{
				int pin = Integer.valueOf((String)json.get("pin"));
				p = pin;
			}catch(Exception problemWhileTryToConvertValueToInt){
				Log.e("Exception while try to convert |pin| value to Int (A)");
				Log.e(problemWhileTryToConvertValueToInt.toString());
				p = UNDEFINED;
			}
		}
		
		if(json.containsKey("type")){
			t = Type.getFromString((String) json.get("type"));
			this.type = t;
		}
		if( (p == UNDEFINED) && (type == Type.PWM) && (json.containsKey("ch")) ){
			try{
				int pin = Integer.valueOf((String)json.get("ch"));
				p = pin;
			}catch(Exception problemWhileTryToConvertValueToInt){
				Log.e("Exception while try to convert |channel| value to Int (B)");
				Log.e(problemWhileTryToConvertValueToInt.toString());
			}
		}else
		if( (p == UNDEFINED) && (type == Type.PWM) && (json.containsKey("channel")) ){
			try{
				int pin = Integer.valueOf((String)json.get("channel"));
				p = pin;
			}catch(Exception problemWhileTryToConvertValueToInt){
				Log.e("Exception while try to convert |channel| value to Int (C)");
				Log.e(problemWhileTryToConvertValueToInt.toString());
			}
		}
		
		if(json.containsKey("reversed")){
			r = (Boolean) json.get("reversed");
		}
		
		init(t,p,r);
	}
	
	/**
	 * Create OutputConfig object from exact parameters.
	 * @param type
	 * @param pin
	 * @param reversed
	 */
	public OutputConfig(int type, int pin, boolean reversed){
		init(type, pin, reversed);
	}
	
	private void init(int type, int pin, boolean reversed){
		if(Type.invalid(type)){
			this.type = Type.INVALID;
			Log.w("OutputConfig.Constructor :: OutputConfig created with invalid type: " + type + " Pin: " + pin);
		}else{
			this.type = type;
		}
		
		this.pin = pin;
		if( (this.type == Type.IO) && (!GPIOHelper.isValidGPIOPin(pin)) ){
			Log.w("OutputConfig.Constructor :: OutputConfig created with invalid GPIO pin: " + pin + " Type: " + this.type);
		}
		
		this.reversed = reversed;
		
		Log.i("Output config used:"
				+ " Type: " + OutputConfig.Type.toString(type)
				+ " " + ((type == Type.PWM)?"Channel: ":"Pin: ") + pin
				+ (reversed?" Reversed":"")
		);
	}
	
	/**
	 * Create OutputConfig object from line (input from config file). <br>
	 * Example input: "PWM 3" or "IO 37"
	 * @param line
	 */
	public OutputConfig(String line){
		
		String[] parts = line.split(" ");
		
		if(parts.length < 2){
			this.type = Type.UNDEFINED;
			this.pin = UNDEFINED;
			Log.w("OutputConfig.Constructor :: Can not parse OutputConfig from input line: |" + line + "|");
			return;
		}
		
		if( parts[0].equalsIgnoreCase(TYPENAME_PWM) ){
			this.type = Type.PWM;
		}else{
			this.type = Type.IO;
		}
		
		try{
			this.pin = Integer.valueOf(parts[1]);
		}catch(NumberFormatException nfe){
			Log.w("OutputConfig.Constructor :: NumberFormatException cought; Line: |" + line + "|");
			this.pin = UNDEFINED;
		}
		
		if( (parts.length > 2) && ((parts[2].contains("reverse")) || (parts[2].contains("Reverse"))) ){
			this.reversed = true;
		}else{
			this.reversed = DEFAULT_VALUE_OF_REVERSED;
		}
		
	}

	public static Pair<String, OutputConfig> createInstaceFromJson(JSONObject json){
		
		if(json == null){
			return null;
		}
		String line = OutputConfig.getLineFrom(json);
		
		if(line == null){
			//TODO warning here
			return null;
		}
		if(!line.contains("=")){
			//TODO warning here
			return null;
		}
		
		String[] parts = line.split("=");
		if(parts.length != 2){
			//TODO Warning here
			return null;
		}
		
		OutputConfig oc = new OutputConfig(parts[1]);
		
		return new Pair<String, OutputConfig>(parts[0], oc);
	}
	
	/**
	 * Create line from JSONOject to initialize OutputConfig Object
	 * @param json JSONObject what contains 1 OutputConfig element.
	 * @return a String from OutputConfig element what can be used for constructor of OutputConfig class.
	 * <br> in case of null parameter it returns null;<br>
	 * <b>Sample output:</b>  
	 */
	public static String getLineFrom(JSONObject json){
		
		if(json == null){
			return null;
		}
		
		if(
			json.containsKey("name") &&
			json.containsKey("type") &&
			(json.containsKey("pin") || json.containsKey("channel"))
		){
			// Contains all mandatory fields
			String dataType = (String) json.get("type");
			if( (!dataType.equalsIgnoreCase(TYPENAME_PWM)) &&(!dataType.equalsIgnoreCase(TYPENAME_IO))){
				dataType = TYPENAME_INVALID;
			}
			
			String dataPin = "UNDEFINED";
			if(json.containsKey("pin")){
				long p = (Long) json.get("pin");
				dataPin = "" + p;
			}else{
				if(json.containsKey("channel")){
					long ch = (Long) json.get("channel");
					dataPin = "" + ch;
				}
			}
			
			boolean isReversed = false;
			if(json.containsKey("reversed")){
				String reversed = (String)json.get("reversed");
				if(reversed.equalsIgnoreCase("no")){
					isReversed = false;
				}else{
					isReversed = true;
				}
			}
			
			//Example output: "PWM 3" or "IO 37" or "IO 38 reversed"
			return json.get("name") + "=" + dataType.toUpperCase() + " " + dataPin + (isReversed?" reversed":"");
			
		}else{
			// Do not contains at least 1 of mandatory fields
			Log.w("OutputConfig can not built from JSON object: " + json);
			String missingMandatoryFields = "";
			if(!json.containsKey("name")){
				missingMandatoryFields += "name, ";
			}
			if(!json.containsKey("type")){
				missingMandatoryFields += "type, ";
			}
			if(!json.containsKey("pin") && !json.containsKey("channel")){
				missingMandatoryFields += "pin or channel";
			}
			Log.w("Mandatory field are missing: " + missingMandatoryFields +"\n");
			return null;
		}
		
	}
	
	
	public int getType() {
		return this.type;
	}

	public int getPin() {
		return this.pin;
	}
	
	public boolean isReversed(){
		return this.reversed;
	}
	
	public String toString(){
		return "Type: "  + Type.toString(this.type) + " " + ((this.type == Type.PWM)?"Channel: ":" Pin: ") + this.pin + (((this.type == Type.IO)&&(this.reversed))?" Reversed":"");
	}
	
	public boolean isValid(){
		return ( (this.getPin()!= UNDEFINED) && (this.getType() != Type.INVALID) && (this.getType() != Type.UNDEFINED));
	}
	
	public boolean isInvalid(){
		return !this.isValid();
	}

	
}
