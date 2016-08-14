package hu.barbar.tasker.util;

import java.util.ArrayList;

import hu.barbar.util.FileHandler;

public class PWMOutputState {
	
	private static final int MIN_CHANNEL_ID =  0;
	
	public static final int MAX_CHANNEL_ID = 15;
	
	public static final int VALUE_UNDEFINED = -1;
	
	public static final int CHANNEL_INVALID = -2;

	private static final String VALUE_SEPARATOR_IN_FILE = "=";

	public static final int CHANNEL_OF_RED = 0;

	public static final int CHANNEL_OF_GREEN = 1;

	public static final int CHANNEL_OF_BLUE = 2;

	public static final String DEFAULT_FILE_NAME = "pwmOutputStates.ini";

	
	private int[] channelValues;
	
	
	
	public static int[] getDefaultChannelValues(){
		return PWMOutputState.getDefaultChannelValues(VALUE_UNDEFINED);
	}
	
	public static int[] getDefaultChannelValues(int defaultValue){
		
		int[] arr = {
			defaultValue, defaultValue, defaultValue, defaultValue,
			defaultValue, defaultValue, defaultValue, defaultValue,
			defaultValue, defaultValue, defaultValue, defaultValue,
			defaultValue, defaultValue, defaultValue, defaultValue
		};
		
		return arr;
		
	}
	
	public PWMOutputState(int[] values) {
		this.channelValues = values;
	}
	
	public PWMOutputState() {
		this.channelValues = PWMOutputState.getDefaultChannelValues();
	}
	
	public int[] getValues(){
		return this.channelValues;
	}
	
	public void setValues(int[] values){
		this.channelValues = values;
	}
	
	
	public void setValue(int channel, int value){
		if(channel < MIN_CHANNEL_ID || channel > MAX_CHANNEL_ID){
			return;
		}
		this.channelValues[channel] = value;
	}
	
	public int getValue(int channel){
		if(channel < MIN_CHANNEL_ID || channel > MAX_CHANNEL_ID){
			return CHANNEL_INVALID;
		}
		return this.channelValues[channel];
	}
	
	public String toString(){
		String s = "PWM output states: ";
		for(int i=0; i<=MAX_CHANNEL_ID; i++){
			s += Integer.toString(this.channelValues[i]);
			if(i < (MAX_CHANNEL_ID) ){
				s += ", ";
			}
		}
		return s;
	}
	
	public ArrayList<String> getFileContent(){
		ArrayList<String> lines = new ArrayList<String>();
		
		for(int i=0; i<=MAX_CHANNEL_ID; i++){
			String s = "ch" + Integer.toString(i) + PWMOutputState.VALUE_SEPARATOR_IN_FILE + this.channelValues[i];
			lines.add(s);
		}
		
		return lines;
	}
	
	/**
	 * Load content from default file.
	 * @return
	 */
	public static int[] loadContentFromFile(){
		return PWMOutputState.loadContentFromFile(Env.getDataFolderPath() + PWMOutputState.DEFAULT_FILE_NAME);
	}
	
	/**
	 * Load content from specified file.
	 * @param path
	 * @return
	 */
	public static int[] loadContentFromFile(String path){
		
		int[] array = getDefaultChannelValues();
		if( !FileHandler.fileExists(path) ){
			return array;
		}
		
		ArrayList<String> lines = FileHandler.readLines(path, true);
		
		if(lines == null || lines.size() == 0){
			return array;
		}

		for(int i = 0; (i < lines.size()) && (i<=MAX_CHANNEL_ID); i++){
			try{
				String[] sa = lines.get(i).split(PWMOutputState.VALUE_SEPARATOR_IN_FILE);
				if(sa.length<2){
					continue;
				}
				array[i] = Integer.valueOf(sa[1]);
			}catch(Exception mayNumberFormatException){}
		}
		
		return array;
	}
	
	/**
	 * Save content to specified file
	 * @param path
	 * @return
	 */
	public boolean saveContentToFile(String path){
		return FileHandler.writeToFile(path, getFileContent());
	}

	/**
	 * Save content to default file
	 * @return
	 */
	public boolean saveContentToFile() {
		return saveContentToFile(Env.getDataFolderPath() + PWMOutputState.DEFAULT_FILE_NAME);
	}
	
	
	public String getContentAsSingleLine(){
		String res = "";
		for(int i=0; i<channelValues.length; i++){
			res += Integer.toString(channelValues[i]);
			if(i<(channelValues.length-1)){
				res += " ";
			}
		}
		return res;
	}
	
}
