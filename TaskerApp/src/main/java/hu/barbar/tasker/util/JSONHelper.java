package hu.barbar.tasker.util;

import org.json.simple.JSONObject;

public class JSONHelper {
	
	public static final String KEY_OF_TYPE_IDENTIFIER = "object";
	
	public class TypeMatchingResult {
		public static final int YES = 1,
								MATCHING = 1,
								MAYBE = 0,
								NO = -1,
								NOT = -1;
		public static final int MINIMUM_MATCHING_LEVEL = MAYBE;
	}

	public static float getFloat(JSONObject json, String key){
		String s = json.get(key) + "";
		float f = Float.valueOf(s.replaceAll(",", "."));
		return f;
	}
	
	public static int getInt(JSONObject json, String key){
		int i = (int) (Float.valueOf(json.get(key) + "") + 0);
		return i;
	}
	
	public static String getString(JSONObject json, String key){
		return (String) (json.get(key) + "");
	}
	
	
	public static int matchingObjectType(JSONObject json, String myType){
		if(json == null){
			return TypeMatchingResult.NO;
		}
		
		if(!json.containsKey(KEY_OF_TYPE_IDENTIFIER)){
			return TypeMatchingResult.MAYBE;
		}
		
		else
		if(JSONHelper.getString(json, KEY_OF_TYPE_IDENTIFIER).equalsIgnoreCase(myType)){
			return TypeMatchingResult.YES;
		}else{
			return TypeMatchingResult.NO;
		}
		
	}
	
}
