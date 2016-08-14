package hu.barbar.comm.util;

/**
 * 
 * @author Barbar
 */
public class RGBMessage extends Msg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9005683801357654801L;

	private Integer colorR = -1,
					colorG = -1,
					colorB = -1;
	
	public RGBMessage(String content, int r, int g, int b) {
		super(content, Msg.Types.RGB_COMMAND);

		int red   = r;
		int green = g;
		int blue  = b;
		
		cribTo8bit(red);
		cribTo8bit(green);
		cribTo8bit(blue);
		
		this.colorR = Integer.valueOf(red);
		this.colorG = Integer.valueOf(green);
		this.colorB = Integer.valueOf(blue);
		
	}
	
	public static RGBMessage createInstance(String line){
		int resolvedR = 0;
		int resolvedG = 0;
		int resolvedB = 0;
		String resolvedContent = "";

		String[] parts = line.split(Msg.ARGUMENT_SEPARATOR);
		for(int i=0; i<parts.length; i++){
			String[] data = parts[i].split("=");
			
			if(data[0].equals("content")){
				resolvedContent = data[1];
			}
			else
			if(data[0].equals("r")){
				resolvedR = Integer.valueOf(data[1]);
			}
			else
			if(data[0].equals("g")){
				resolvedG = Integer.valueOf(data[1]);
			}
			else
			if(data[0].equals("b")){
				resolvedB = Integer.valueOf(data[1]);
			}

		}
		return new RGBMessage(resolvedContent, resolvedR, resolvedG, resolvedB);
	}

	public String getInstanceAsLine(){
		return getParameterLine("content", content) + Msg.ARGUMENT_SEPARATOR
			 + getParameterLine("type", this.type + "") + Msg.ARGUMENT_SEPARATOR
			 + getParameterLine("r", this.colorR + "") + Msg.ARGUMENT_SEPARATOR
			 + getParameterLine("g", this.colorG + "") + Msg.ARGUMENT_SEPARATOR
			 + getParameterLine("b", this.colorB + "")
		;
	}
	
	public boolean equals(RGBMessage otherInstance){
		if(this.type != otherInstance.getType()){
			return false;
		}
		if(!this.content.equals(otherInstance.getContent())){
			return false;
		}
		if( (this.getRed()   != otherInstance.getRed())   ||
			(this.getGreen() != otherInstance.getGreen()) ||
			(this.getBlue()  != otherInstance.getBlue())  ){
			return false;
		}
		return true;
	}

	private static int cribTo8bit(int val){
		if(val < 0)
			return 0;
		if(val > 255)
			return 255;
		return val;
	}

	public int getRed(){
		return this.colorR;
	}
	
	public int getGreen(){
		return this.colorG;
	}
	
	public int getBlue(){
		return this.colorB;
	}

	
	protected String getColorCompontents(){
		return "R: " + this.colorR +
			   " G: " + this.colorG +
			   " B: " + this.colorB;
	}
	
	public String getColorHex(){
		return Integer.toHexString(getRed()) + Integer.toHexString(getGreen()) + Integer.toHexString(getBlue());
	}
	
	public String getColorHexWithTitle(){
		return "Color: " + this.getContent();
	}
	
	@Override
	public String toString(){
		return super.getFirstPartOfToString() +
				getColorCompontents() + 
				super.getLastPartOfToString();
	}
	
}
