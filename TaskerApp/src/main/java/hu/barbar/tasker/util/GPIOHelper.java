package hu.barbar.tasker.util;

public class GPIOHelper {
	
	public static final int MAX_PIN_NUMBER = 40;
	
	private static int[] validGPIOPins = {3, 5, 7, 8, 10, 11, 12, 13, 15, 16, 18, 19, 21, 22, 23, 24, 26, 29, 31, 32, 33, 35, 36, 37, 38, 40};
	
	
	public static boolean isValidGPIOPin(int pin){
		return isElementOfArray(validGPIOPins, pin);
	}
	
	private static boolean isElementOfArray(int[] arr, int element){
		for(int i=0; i<arr.length; i++){
			if(arr[i] == element){
				return true;
			}
		}
		return false;
	}
	
}
