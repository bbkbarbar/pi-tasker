package hu.barbar.tasker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import hu.barbar.tasker.log.IOLogger;
import hu.barbar.tasker.util.Env;
import hu.barbar.tasker.util.ExternalResources;
import hu.barbar.tasker.util.PWMOutputState;
import hu.barbar.util.logger.Log;

public class TaskExecutor {

	
	public static String readTemp(String param){
		
		String response = "_PROBLEM_";
		String cmd = "python " + ExternalResources.SCRIPT_PATH + ExternalResources.TEMPERATURE_READER_SCRIPT + " -s -1";
		if(Env.runningOnTargetDevice()){
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				
				//TODO: megcsinalni h opcionalis legyen az ilyen "log-ok" kiirasa
				if(Tasker.DEBUG_MODE){
					Log.d("Task execution (readTemp) completed");
				}
				
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				response = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.d("<<RUN PYTHON SCRIPT>>\n" + cmd);
			response = "2 12.234 26.027";
		}
		
		return response;
	}

	/**
	 * Get CPU temp from system.
	 * @return a String line e.g.: "CPU temp: 41.2 C" 
	 * <br>// id of value part: 2
	 */
	public static String readCPUTemp(){
		
		String response = "_PROBLEM_";
		String cmd = "python " + ExternalResources.SCRIPT_PATH + ExternalResources.CPU_TEMPERATURE_READER_SCRIPT;
		if(Env.runningOnTargetDevice()){
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				
				if(Tasker.DEBUG_MODE){
					Log.d("Task execution (get cpu temp) completed");
				}
				
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				response = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Log.d("<<RUN PYTHON SCRIPT>>\n" + cmd);
			response = "CPU temp: 11.2 C";
		}
		
		return response;
	}
	
	public static void setAllPwmOutputs(int[] outputContent, boolean needToSave){
		
		String args = "";
		
		if(outputContent[PWMOutputState.CHANNEL_OF_RED] != PWMOutputState.VALUE_UNDEFINED){
			args += " -r " + outputContent[PWMOutputState.CHANNEL_OF_RED];
			Log.d("PWM [0]: " + outputContent[PWMOutputState.CHANNEL_OF_RED]);
		}
		
		if(outputContent[PWMOutputState.CHANNEL_OF_GREEN] != PWMOutputState.VALUE_UNDEFINED){
			args += " -g " + outputContent[PWMOutputState.CHANNEL_OF_GREEN];
			Log.d("PWM [1]: " + outputContent[PWMOutputState.CHANNEL_OF_GREEN]);
		}
		
		if(outputContent[PWMOutputState.CHANNEL_OF_BLUE] != PWMOutputState.VALUE_UNDEFINED){
			args += " -b " + outputContent[PWMOutputState.CHANNEL_OF_BLUE];
			Log.d("PWM [2]: " + outputContent[PWMOutputState.CHANNEL_OF_BLUE]);
		}
		
		for(int i = 3; i<=PWMOutputState.MAX_CHANNEL_ID; i++){
			if(outputContent[i] != PWMOutputState.VALUE_UNDEFINED){
				args += " -ch" + i 	+ " " + outputContent[i];
				Log.d("PWM [" + i + "]: " + outputContent[i]);
			}
		}
		
		
		//RUN SCRIPT
		String cmd = "python " + ExternalResources.SCRIPT_PATH + ExternalResources.COLOR_OUTPUT_CONTROL_SCRIPT
				+ args;
		
		if(Env.runningOnTargetDevice()){
			try {
				//Process p = Runtime.getRuntime().exec(cmd);
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				Log.e("Can not execute \"setColor\" script.");
			}
		}else{
			Log.i("<<RUN PYTHON SCRIPT>>\n" + cmd);
		}
		
		Tasker.pwmOutputStates.setValues(outputContent);
		if( needToSave ){
			Tasker.pwmOutputStates.saveContentToFile();
		}
	}
	
	public static void setColor(int red, int green, int blue) {
		
		int redIn16bit   = keepValueInRange(red   * 16, 0, 4095);
		int greenIn16bit = keepValueInRange(green * 16, 0, 4095);
		int blueIn16bit  = keepValueInRange(blue  * 16, 0, 4095);
		
		Tasker.pwmOutputStates.setValue(PWMOutputState.CHANNEL_OF_RED,   redIn16bit);
		Tasker.pwmOutputStates.setValue(PWMOutputState.CHANNEL_OF_GREEN, greenIn16bit);
		Tasker.pwmOutputStates.setValue(PWMOutputState.CHANNEL_OF_BLUE,  blueIn16bit);
		
		TaskExecutor.setAllPwmOutputs(Tasker.pwmOutputStates.getValues(), true);
		
	}
		
	public static void setPwmOutput(int pin, int outputValue) {
		if( Tasker.pwmOutputStates.getValue(pin) != outputValue ){
			Tasker.pwmOutputStates.setValue(pin, outputValue);
			TaskExecutor.setAllPwmOutputs(Tasker.pwmOutputStates.getValues(), true);
		}
	}


	public static void setIOState(int pinNum, boolean state){
		
		//TODO: !!! check pin validation
		
		String cmd = "python " + ExternalResources.SCRIPT_PATH + ExternalResources.IO_OUTPUT_SCRIPT +" -p " + pinNum + " -s " + (state?1:0);
		
		Log.d("setIOState -> command: |" + cmd + "|");
		
		if(Env.runningOnTargetDevice()){
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				
				if(Tasker.DEBUG_MODE){
					Log.d("Task execution (readTemp) completed");
				}
				
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				//String response = in.readLine();
				in.readLine();
				
				Tasker.ioOutputStates[pinNum] = (state?1:0);
				                      
				IOLogger.add(pinNum, state);
			} catch (IOException e) {
				Log.e("TaskExecutor :: Exception cought when tried to set output pin: " + e.toString());
				e.printStackTrace();
			}
		}else{
			Log.i("<<RUN PYTHON SCRIPT>>\n" + cmd);
		}
		
	}

	
	private static int keepValueInRange(int value, int min, int max){
		if(value > max){
			return max;
		}
		if(value < min){
			return min;
		}
		return value;
	}
	
}
