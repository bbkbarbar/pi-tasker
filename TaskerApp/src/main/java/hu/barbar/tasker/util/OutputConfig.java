package hu.barbar.tasker.util;

import hu.barbar.util.logger.Log;

public class OutputConfig {
	
	public static final int UNDEFINED = -1;
	
	public static final int INVALID = -2;
	
	public static final String TYPENAME_PWM = "pwm";

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
	
	
	/**
	 * Create OutputConfig object from exact parameters.
	 * @param type
	 * @param pin
	 */
	public OutputConfig(int type, int pin){
		if(Type.invalid(type)){
			this.type = Type.INVALID;
			Log.w("OutputConfig.Constructor :: OutputConfig created with invalid type: " + type);
		}else{
			this.type = type;
		}
		
		this.pin = pin;
		if( (this.type == Type.PWM) && (!GPIOHelper.isValidGPIOPin(pin)) ){
			Log.w("OutputConfig.Constructor :: OutputConfig created with invalid GPIO pin: " + pin);
		}
		
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
		
	}

	
	public int getType() {
		return type;
	}

	public int getPin() {
		return pin;
	}
	
	public String toString(){
		return "Type: "  + Type.toString(this.type) + " " + ((this.type == Type.PWM)?"Channel: ":" Pin: ") + this.pin;
	}
	
	public boolean isValid(){
		return ( (this.getPin()!= UNDEFINED) && (this.getType() != Type.INVALID) && (this.getType() != Type.UNDEFINED));
	}
	
	public boolean isInvalid(){
		return !this.isValid();
	}

	
}
