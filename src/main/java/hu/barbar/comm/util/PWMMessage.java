package hu.barbar.comm.util;

public class PWMMessage extends Msg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 156846236519234936L;

	private static final int CHANNEL_UNDEFINED = -1;
	
	private static final float VALUE_UNDEFINED = -1f;

	private static final float DEFAULT_MAX_VALUE = 100f;
	
	private static float MAX_VALUE = DEFAULT_MAX_VALUE;
	
	
	private int channel = CHANNEL_UNDEFINED;
	
	private float value = VALUE_UNDEFINED;
	
	
	public PWMMessage(int channel, float value) {
		super("setOutput", Msg.Types.PWM_COMMAND);
		this.channel = channel;
		this.value = value;
		
	}


	public int getChannelID() {
		return channel;
	}

	public float getValue() {
		return value;
	}

	public static float getMaxValue(){
		return PWMMessage.MAX_VALUE;
	}
	
	
	public void setChannelID(int channelID) {
		this.channel = channelID;
	}

	public void setValue(float value) {
		if(value > PWMMessage.MAX_VALUE){
			this.value = PWMMessage.MAX_VALUE;
		}else{
			this.value = value;
		}
	}
	
	public static void setMaxValue(float maxValue){
		PWMMessage.MAX_VALUE = maxValue;
	}

	protected String getPWMInfo(){
		return " Channel: " + this.channel + " Value: " + this.getValue();
	}
	
	@Override
	public String toString(){
		return super.getFirstPartOfToString() +
				getPWMInfo() + 
				super.getLastPartOfToString();
	}
	
}
