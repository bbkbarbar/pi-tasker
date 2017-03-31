package hu.barbar.PiTasker.util.usagelog;

import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Test;

import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.OutputState;
import junit.framework.TestSuite;

public class OutputStateTest extends TestSuite {
	
	JSONObject json = null;
	
	@Test
	public void pwmStateToJSON(){
		
		OutputState item = new OutputState(1, 255, OutputConfig.Type.PWM);
		json = item.getAsJSON();
		System.out.println("|" + json + "|");
		assertEquals("{\"channel\":1,\"type\":2,\"value\":255}", item.getAsJSON().toString());
		
		OutputState item2= new OutputState(json);
		assertEquals(1, item2.getChannel());
		assertEquals(255, item2.getValue());
		assertEquals(OutputConfig.Type.PWM, item2.getType());
		
	}
	
}