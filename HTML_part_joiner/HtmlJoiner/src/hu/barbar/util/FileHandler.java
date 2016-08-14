package hu.barbar.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandler {
	
	public static final String DEFAULT_CONFIG_SEPARATOR = "=";
	
	public static final int RESULT_EXISTS = 0,
							RESULT_CREATED = 1,
							RESULT_CAN_NOT_CREATED = -1;
	
	
	public static ArrayList<String> readLines(String path){
		return readLines(path, false);
	}
	
	public static ArrayList<String> readLines(String path, boolean needTrim){
		
		if(path == null || path.trim().equals("")){
			return null;
		}
		
		ArrayList<String> lines = new ArrayList<>();
		String currentLine;
		BufferedReader br;
		
		try {
			
			br = new BufferedReader(new FileReader(path));
			while ((currentLine = br.readLine()) != null) {
				if( !needTrim || (needTrim && !currentLine.trim().equals(""))){
					lines.add(currentLine);
				}
			}
			
		} catch (FileNotFoundException e) {
			//TODO: megcsinlani h ezek a warningok / error-ok ertelmesen legyenek kezelve
			System.out.println("File not found: " + path);
		} catch (IOException e) {
			System.out.println("IOException while try to read file:\n");
			System.out.println(path);
		}
		
		return lines;
		
	}
	
	
	public static boolean writeToFile(String destinationFile, ArrayList<String> content) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(destinationFile));
	 
			for (int i = 0; i < content.size(); i++) {
				pw.write(content.get(i) + "\n");
			}
		 
			pw.close();
		} catch (IOException e) {
			//TODO: log
			return false;
		}
		return true;
	}
	
	public static boolean appendToFile(String destinationFile, String line){
		PrintWriter  output;
		
		if(!fileExists(destinationFile)){
			ArrayList<String> contentOfNewFile = new ArrayList<>();
			contentOfNewFile.add(line);
			return writeToFile(destinationFile, contentOfNewFile);
		}else{
			try {
				output = new PrintWriter(new BufferedWriter(new FileWriter(destinationFile, true)));
				output.append(line + "\n");
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}  //clears file every time
			return true;
		}
			
	}
	
	
	public static HashMap<String, String> readConfig(String path){
		return readConfig(path, FileHandler.DEFAULT_CONFIG_SEPARATOR);
	}
	
	//TODO: TEST IT!!
	public static HashMap<String, String> readConfig(String path, String separator){
		
		ArrayList<String> lines = FileHandler.readLines(path);
		if(lines == null){
			return null;
		}
		
		HashMap<String, String> result = new HashMap<>();
		
		for(int i=0; i<lines.size(); i++){
			String[] arr = lines.get(i).split(separator);
			
			// Drop line if not contains separator "="
			
			if(arr.length == 2){
				result.put(arr[0], arr[1]);
			}
			else
			if(arr.length > 2){
				String value = "";
				for(int j=1; j<arr.length; j++){
					value += arr[j];
					if(j<arr.length-1){
						value += separator;
					}
				}
				result.put(arr[0], value);
			}
		}
		
		return result;
		
	}
	
	
	public static boolean fileExists(String filePath){ 
		File f = new File(filePath);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	
	/**
	 * Check that folder exists or not.
	 * @param path
	 * @return FileHandler.RESULT_EXISTS OR <br>
	 *         FileHandler.RESULT_CREATED if not exists but could be created now OR <br>
	 *         FileHandler.RESULT_CAN_NOT_CREATED if does not exists and can not be created.
	 */
	public static int createFolderIfNotExists(String path){
		File theDir = new File(path);

		if (theDir.exists()) {
			return RESULT_EXISTS;
		}else{
			// if the directory does not exist, create it
			//TODO: log if needed!:
		    //System.out.println("Creating directory: " + path);
	
		    try{
		        theDir.mkdir();
		        return RESULT_CREATED;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }
	    	//TODO: log if needed!:
		    //System.out.println("Can not create directory!!!");
		    return RESULT_CAN_NOT_CREATED;
		}
	}
	
}
