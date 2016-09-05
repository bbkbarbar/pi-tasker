package hu.barbar.tasker.util;

import java.io.Serializable;

public class OutputState implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1442222406637555178L;
	
	public static final int OUTPUT_STATE_UNABLE_TO_FIND = -999;
	
	private int value;
	
	private int type;
	
	
	public OutputState(int val, int type){
		this.value = val;
		this.type = type;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public int getType(){
		return this.type;
	}
	
	public boolean isUnableToFind(){
		return (this.value == OutputState.OUTPUT_STATE_UNABLE_TO_FIND);
	}
	
	public String toString(){
		return "" + this.value + " " + OutputConfig.Type.toString(this.getType());
	}
	
}