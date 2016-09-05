package hu.barbar.tasker.util;

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
			Log.w("OutputConfig.Constructor :: OutputConfig created with invalid type: " + type);
		}else{
			this.type = type;
		}
		
		this.pin = pin;
		if( (this.type == Type.IO) && (!GPIOHelper.isValidGPIOPin(pin)) ){
			Log.w("OutputConfig.Constructor :: OutputConfig created with invalid GPIO pin: " + pin);
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
		
		String value = parts[1].trim();
		String[] valueParts = value.split(" ");
		
		try{
			this.pin = Integer.valueOf(valueParts[0]);
		}catch(NumberFormatException nfe){
			Log.w("OutputConfig.Constructor :: NumberFormatException cought; Line: |" + line + "|");
			this.pin = UNDEFINED;
		}
		
		if( (valueParts.length > 1) && (valueParts[1].equalsIgnoreCase("REVERSED")) ){
			this.reversed = true;
		}else{
			this.reversed = DEFAULT_VALUE_OF_REVERSED;
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
