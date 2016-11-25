package hu.barbar.PiTasker.util.usagelog;

import static org.junit.Assert.*;

import java.util.Date;

import org.json.simple.JSONObject;
import org.junit.Test;

import hu.barbar.tasker.todo.items.OutputEventScheduler;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.OutputState;
import hu.barbar.tasker.util.usagelog.UsageLog;
import hu.barbar.tasker.util.usagelog.UsageLogItem;
import junit.framework.TestSuite;

public class OutputEventSchedulerTest extends TestSuite {
	
	JSONObject json = null;
	
	@Test
	public void createFromJSONTest(){
		
		OutputEventScheduler myScheduler = new OutputEventScheduler("..\\onTarget\\home_-_pi\\taskerData\\timedPwmEvents.json");
		
	}
	
}