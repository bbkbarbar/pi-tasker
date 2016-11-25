package hu.barbar.tasker.util;

import java.io.Serializable;

import org.json.simple.JSONObject;
import hu.barbar.tasker.util.OutputConfig.Type;

public class OutputState implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1442222406637555178L;
	
	public static final int OUTPUT_STATE_UNABLE_TO_FIND = -999;
	
	public static final int CHANNEL_UNDEFINED = -1;
	public static final int VALUE_UNDEFINED = -1;
	
	public static final String KEY_CHANNEL = "channel";
	public static final String KEY_PIN = "pin";
	public static final String KEY_VALUE = "value";
	public static final String KEY_TYPE = "type";
	
	
	
	private long channel = CHANNEL_UNDEFINED;
	
	private long value = VALUE_UNDEFINED;
	
	private long type = Type.UNDEFINED;
	
	
	
	public OutputState(int val, int type){
		this.value = val;
		this.type = type;
	}
	
	public OutputState(int ch, int val, long type){
		this.channel = ch;
		this.value = val;
		this.type = type;
	}
	
	public OutputState(JSONObject json) {
		
		if(json.containsKey(KEY_CHANNEL)){
			this.channel = (Long) json.get(KEY_CHANNEL);
		}else{
			if(json.containsKey(KEY_PIN)){
				this.channel = (Long) json.get(KEY_PIN);
			}else{
				this.channel = CHANNEL_UNDEFINED;
			}
		}
		
		if(json.containsKey(KEY_VALUE)){
			this.value = (Long) json.get(KEY_VALUE);
		}else{
			this.value = VALUE_UNDEFINED;
		}
		
		if(json.containsKey(KEY_TYPE)){
			String str = ("" + json.get(KEY_TYPE)).trim();
			if(str.equalsIgnoreCase("pwm")){
				this.type = Type.PWM;
			} else 
			if(str.equalsIgnoreCase("io")){
				this.type = Type.IO;
			}
		}else{
			this.type = Type.UNDEFINED;
		}
		
	}
		
	public long getChannel(){
		return this.channel;
	}
	
	public long getPin(){
		return this.channel;
	}
	
	public long getValue(){
		return this.value;
	}
	
	public int getType(){
		return (int) this.type;
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject getAsJSON(){
		JSONObject meAsJson = new JSONObject();
		
		
		meAsJson.put(KEY_TYPE, this.type);
		
		if(type == Type.IO){
			meAsJson.put(KEY_PIN, this.channel);
		}else{
			if(type == Type.PWM){
				meAsJson.put(KEY_CHANNEL, this.channel);
			}
		}
		
		meAsJson.put(KEY_VALUE, this.value);
		
		return meAsJson;
	}

	
	public void setChannel(int ch){
		this.channel = ch;
	}
	
	public void setPin(int pin){
		this.channel = pin;
	}	
	
	public void setValue(int val){
		this.value = val;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	
	public boolean isUnableToFind(){
		return (this.value == OutputState.OUTPUT_STATE_UNABLE_TO_FIND);
	}
	
	public String toString(){
		return "" + this.value + " " + Type.toString(this.getType());
	}
	
}