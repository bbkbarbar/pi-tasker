package hu.barbar.tasker.util;

import org.json.simple.JSONObject;

import hu.barbar.util.logger.Log;

public class OutputConfig {
	
	public static final int UNDEFINED = -1;
	
	public static final int INVALID = -2;
	
	public static final String TYPENAME_PWM = "pwm";
	
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
			if(text.equalsIgnoreCase("IO")){
				return IO;
			}
			else
			if(text.equalsIgnoreCase("PWM")){
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
					return "IO";
				case PWM:
					return "PWM";
					
				default:
					return "INVALID";
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
		
		if(json.containsKey("pin")){
			try{
				int pin = (Integer) json.get("pin");
				p = pin;
			}catch(Exception problemWhileTryToConvertValueToInt){
				p = UNDEFINED;
			}
		}
		
		if(json.containsKey("type")){
			t = Type.getFromString((String) json.get("type"));
			this.type = t;
		}
		if( (p == UNDEFINED) && (type == Type.PWM) && (json.containsKey("ch")) ){
			try{
				int pin = (Integer) json.get("ch");
				p = pin;
			}catch(Exception problemWhileTryToConvertValueToInt){}
		}else
		if( (p == UNDEFINED) && (type == Type.PWM) && (json.containsKey("channel")) ){
			try{
				int pin = (Integer) json.get("channel");
				p = pin;
			}catch(Exception problemWhileTryToConvertValueToInt){}
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
		Log.t(this.toString());
		
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
