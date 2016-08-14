package hu.barbar.tasker.util;

public class TemperatureResult {
	
	public static final float INITIAL_TEMP_VALUE = 95.000f;
	
	public static final float UNDEFINED_VALUE = -999.000f;
	
	public static final float TEMPERATURE_VALUE_NOT_CORRENT = 85.0f;
	
	private static final String SEPARATOR_IN_PYTHON_SCRIPT = " ";

	private static final int IDX_WATER = 0;

	private static final int IDX_AIR = 1;
	
	private float[] values = null;
	
	private boolean weHaveAtLeastOneUseableValue = false;
	
	public TemperatureResult(String lineFromPythonScript) {
		String[] parts = lineFromPythonScript.split(SEPARATOR_IN_PYTHON_SCRIPT);
		
		//	2 temp sensor available
		if(parts[0].equals("2")){
			float[] res = {INITIAL_TEMP_VALUE, INITIAL_TEMP_VALUE};
			res[0] = Float.valueOf(parts[1]);
			res[1] = Float.valueOf(parts[2]);
			values = res;
			weHaveAtLeastOneUseableValue = true;
		}
		else
		//	only 1 temp sensor available
		if(parts[0].equals("1")){
			float[] res = {INITIAL_TEMP_VALUE, INITIAL_TEMP_VALUE};
			res[0] = Float.valueOf(parts[1]);
			values = res;
			weHaveAtLeastOneUseableValue = true;
		}
		
	}
	
	public boolean hasValue(){
		return this.weHaveAtLeastOneUseableValue;
	}
	
	public boolean hasMultipleValues(){
		if(weHaveAtLeastOneUseableValue == false || values == null){
			return false;
		}
		if(values.length>1){
			return true;
		}else{
			return false;
		}
	}

	private static boolean isNear(float value, float point, float distance){
		if( (value >= (point-distance)) && (value <= (point+distance)) ){
			return true;
		}
		return false;
	}
	
	public static boolean isValueValid(float value){
		
		if( (value < -100f) ||
			(isNear(value, TemperatureResult.UNDEFINED_VALUE, 0.05f)) ||
			(isNear(value, TemperatureResult.INITIAL_TEMP_VALUE, 0.05f)) ||
			(value == TemperatureResult.TEMPERATURE_VALUE_NOT_CORRENT) ||
			(value > 970f)
		){
			return false;
		}
		
		return true;
	}
	
	public float getValue(int idx){
		if(values == null || values.length == 0 || idx > (values.length-1) || idx < 0){
			return TemperatureResult.UNDEFINED_VALUE;
		}
		
		if(isValueValid(values[idx])){
			return values[idx];
		}else{
			return TemperatureResult.UNDEFINED_VALUE;
		}
	}
	
	
	public float getTempOfWater(){
		return this.getValue(IDX_WATER);
	}
	
	public float getTempOfAir(){
		if(values.length == 2){
			return this.getValue(IDX_AIR);
		}else{
			return this.getValue(0);
		}
	}

	public static boolean isValueInvalid(float tempValue) {
		return !(isValueValid(tempValue));
	}

}
