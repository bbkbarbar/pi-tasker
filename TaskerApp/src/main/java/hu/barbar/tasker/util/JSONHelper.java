package hu.barbar.tasker.util;

import org.json.simple.JSONObject;

public class JSONHelper {

	public static float getFloat(JSONObject json, String key){
		String s = json.get(key) + "";
		float f = Float.valueOf(s.replaceAll(",", "."));
		return f;
	}
	
	public static int getInt(JSONObject json, String key){
		int i = (int) (Float.valueOf(json.get(key) + "") + 0);
		return i;
	}
	
}
