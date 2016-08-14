package hu.barbar.util;

import java.util.ArrayList;
import java.util.HashMap;

import hu.barbar.tasker.util.OutputConfig;

public class TaskerFilehandler extends FileHandler {

	public static HashMap<String, OutputConfig> readOutputConfig(String path){
		ArrayList<String> lines = FileHandler.readLines(path);
		if(lines == null){
			return null;
		}
		
		HashMap<String, OutputConfig> result = new HashMap<>();
		
		for(int i=0; i<lines.size(); i++){
			String[] arr = lines.get(i).split("=");
			
			// Drop line if not contains separator "="
			
			if(arr.length == 2){
				result.put(arr[0].trim(), new OutputConfig( arr[1].trim() ));
			}
		}
		
		return result;
	}
	
}
