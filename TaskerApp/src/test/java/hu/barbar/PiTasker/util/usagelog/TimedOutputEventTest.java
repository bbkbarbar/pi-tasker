package hu.barbar.PiTasker.util.usagelog;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import hu.barbar.tasker.todo.items.TimedOutputEvent;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.util.FileHandler;
import junit.framework.TestSuite;

public class TimedOutputEventTest extends TestSuite {
	
	
	@Test
	public void loadFromJsonTest(){
		
		//JSONObject json = FileHandler.readJSON("d:\\workspace_j\\pi-tasker\\onTarget\\home_-_pi\\taskerData\\timedPwmEvents.json");
		JSONObject json = FileHandler.readJSON("..\\onTarget\\home_-_pi\\taskerData\\scheduledOutputEvents.json");
		JSONArray ja = (JSONArray) json.get("timedOutput_events");
		JSONObject stateItem = (JSONObject) ja.get(0);
		System.out.println("|" + stateItem + "|");
		TimedOutputEvent item = new TimedOutputEvent(stateItem);

		assertEquals(639, item.getTime());
		assertEquals(OutputConfig.Type.PWM, item.getType());
		assertEquals(0,   item.getOutputState(0).getChannel());
		assertEquals(3900, item.getOutputState(0).getValue());
		assertEquals(2,   item.getOutputState(2).getChannel());
		assertEquals(3900, item.getOutputState(2).getValue());
		
		
		JSONObject stateItem3 = (JSONObject) ja.get(1);
		System.out.println("|" + stateItem3 + "|");
		TimedOutputEvent item3 = new TimedOutputEvent(stateItem3);

		assertEquals(630,  item3.getTime());
		assertEquals(OutputConfig.Type.IO, item3.getType());
		assertEquals(38,   item3.getOutputState(0).getPin());
		assertEquals(0,    item3.getOutputState(0).getValue());
		
		
		JSONObject stateItem2 = (JSONObject) ja.get(2);
		System.out.println("|" + stateItem2 + "|");
		TimedOutputEvent item2 = new TimedOutputEvent(stateItem2);

		assertEquals(2230, item2.getTime());
		assertEquals(0,    item2.getOutputState(0).getChannel());
		assertEquals(1950,    item2.getOutputState(0).getValue());
		assertEquals(2,    item2.getOutputState(2).getChannel());
		assertEquals(1800,    item2.getOutputState(2).getValue());
		
	}
	
	/*
	@Test
	public void getHoursAndMinutesTest(){
		
		int zuluZime = TimedOutputEvent.getZuluTime(1479807049263l); 	//10:30:49
		assertEquals(1030, zuluZime);
	}/**/
	
	@Test
	public void needToDoNowTest(){
		
		JSONObject json = FileHandler.readJSON("..\\onTarget\\home_-_pi\\taskerData\\scheduledOutputEvents.json");
		JSONArray ja = (JSONArray) json.get("timedOutput_events");
		JSONObject stateItem = (JSONObject) ja.get(0);
		
		stateItem.put("time", 1030l);
		TimedOutputEvent item = new TimedOutputEvent(stateItem);
		
		assertTrue(item.needToDoNow(1479807049263l));
	}
	
	@Test
	public void needToDoNowFalseTest1(){
		
		JSONObject json = FileHandler.readJSON("..\\onTarget\\home_-_pi\\taskerData\\scheduledOutputEvents.json");
		JSONArray ja = (JSONArray) json.get("timedOutput_events");
		JSONObject stateItem = (JSONObject) ja.get(0);
		
		stateItem.put("time", 1029l);
		TimedOutputEvent item = new TimedOutputEvent(stateItem);
		
		assertFalse(item.needToDoNow(1479807049263l));
	}
	
	@Test
	public void needToDoNowFalseTest2(){
		
		JSONObject json = FileHandler.readJSON("..\\onTarget\\home_-_pi\\taskerData\\scheduledOutputEvents.json");
		JSONArray ja = (JSONArray) json.get("timedOutput_events");
		JSONObject stateItem = (JSONObject) ja.get(0);
		
		stateItem.put("time", 1031l);
		TimedOutputEvent item = new TimedOutputEvent(stateItem);
		
		assertFalse(item.needToDoNow(1479807049263l));
	}
	
}