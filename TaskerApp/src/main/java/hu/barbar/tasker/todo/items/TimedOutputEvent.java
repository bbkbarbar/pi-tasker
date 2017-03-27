package hu.barbar.tasker.todo.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hu.barbar.tasker.util.OutputState;
import hu.barbar.util.logger.Log;
import hu.barbar.tasker.util.OutputConfig.Type;

public class TimedOutputEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7901012118743427671L;

	private static final String KEY_TIME = "time";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PWM_STATES = "pwmStates";
	private static final String KEY_IO_STATES =  "ioStates";
	

	
	private long time;
	
	private int type; 
	
	private ArrayList<OutputState> outputStates = null;
	
	
	public TimedOutputEvent(JSONObject json) {
		
		if(json.containsKey(KEY_TIME)){
			this.time = (Long) json.get(KEY_TIME);
		}else{
			this.time = -1;
		}
		
		if(json.containsKey(KEY_TYPE)){
			String str = ((String) json.get(KEY_TYPE)).trim();
			
			if(str.equalsIgnoreCase("pwm")){
				this.type = Type.PWM;
				
				if(json.containsKey(KEY_PWM_STATES)){
					outputStates = new ArrayList<OutputState>();
					JSONArray jArr = (JSONArray) json.get(KEY_PWM_STATES);
					for(int i=0; i<jArr.size(); i++){
						JSONObject obj = (JSONObject) jArr.get(i);
						if(obj.containsKey(OutputState.KEY_CHANNEL) && obj.containsKey(OutputState.KEY_VALUE)){
							OutputState ps = new OutputState(obj);
							outputStates.add(ps);
						}
					}
				}
				
			} else 
			if(str.equalsIgnoreCase("io")){
				this.type = Type.IO;
				
				if(json.containsKey(KEY_IO_STATES)){
					outputStates = new ArrayList<OutputState>();
					JSONArray jArr = (JSONArray) json.get(KEY_IO_STATES);
					for(int i=0; i<jArr.size(); i++){
						JSONObject obj = (JSONObject) jArr.get(i);
						if(obj.containsKey(OutputState.KEY_PIN) && obj.containsKey(OutputState.KEY_VALUE)){
							OutputState ps = new OutputState(obj);
							outputStates.add(ps);
						}
					}
				}
				
			}
		}else{
			this.type = Type.UNDEFINED;
		}
		
	}
	
	
	public long getTime(){
		return this.time;
	}
	
	public long getType(){
		return this.type;
	}
	
	public OutputState getOutputState(int idx){
		if(this.outputStates != null && this.outputStates.size() >idx) {
			return this.outputStates.get(idx);
		} else {
			return null;
		}
	}
	
	public int getgetOutputStateCount(){
		if(this.outputStates == null){
			return -1;
		}
		return this.outputStates.size();
	}
	
	public boolean needToDoNow(long timeInMs){
		boolean need = (this.time == getZuluTime(timeInMs));
		Log.d("NeedToRun? -> " + need);
		return (this.time == getZuluTime(timeInMs));
	}

	public String toString() {
		return "Time: " + time + " type: " + type + " OutputState count: " + this.getgetOutputStateCount();
	}
	

	/**
	 * Get specific time in "Zulu" format regardless of the daylight saving time 
	 * @param timeInMs the specific time (from currentTimeInMs() -function.
	 * @return the specific time in "Zulu" format. <br>
	 * 		   e.g:                                <br>
	 * 		     Time:   12:37:51                  <br>
	 * 			 Result: 1237
	 */
	private static int getZuluTime(long timeInMs){
		SimpleDateFormat zuluTimeSDF = new SimpleDateFormat("HHmm");
		Log.d("Zulu time: " + zuluTimeSDF.format(new Date(timeInMs)));
		return Integer.valueOf(zuluTimeSDF.format(new Date(timeInMs)));
	}

	// TODO: delete the old version of this function after successful tests of the new version on target device too..
	private static int getZuluTimeOLD(long timeInMs){
		
		Calendar cal = Calendar.getInstance();
		long milliDiff = cal.get(Calendar.ZONE_OFFSET);
		
		long ms = timeInMs + milliDiff;
		
		//int seconds = (int) (ms / 1000) % 60 ;
		int minutes = (int) ((ms / (1000*60)) % 60);
		int hours   = (int) ((ms / (1000*60*60)) % 24);
		
		//System.out.println(" " + hours + ":" + minutes + ":" + seconds);
		
		return (hours*100) + (minutes);
		
	}

}
