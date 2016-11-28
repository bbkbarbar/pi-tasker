package hu.barbar.tasker.todo.items;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hu.barbar.tasker.TaskExecutor;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.util.FileHandler;

public class OutputEventScheduler extends ToDoItemBase {

	private ArrayList<TimedOutputEvent> events = null;
	
	public OutputEventScheduler(String filePathOfJSON) {
		super();
		
		JSONObject json = FileHandler.readJSON(filePathOfJSON);
		
		if(json.containsKey("timedOutput_events")){
			events = new ArrayList<TimedOutputEvent>();
			JSONArray eventArray = (JSONArray) json.get("timedOutput_events");
			for(int i=0; i<eventArray.size(); i++){
				TimedOutputEvent e = new TimedOutputEvent((JSONObject) eventArray.get(i));
				//System.out.println( e.toString() );
				events.add(e);
				
			}
		}
		
		
	}
	
	
	@Override
	public String getClassTitle() {
		return "OutputEvent Scheduler";
	}

	@Override
	public void execute() {
		
		ArrayList<TimedOutputEvent> executionList = new ArrayList<TimedOutputEvent>();
		
		/*
		 * Select what events we should to execute now
		 * and store them in a separated temporary list.
		 */
		long timeInMs = System.currentTimeMillis();
		for(int i=0; i<events.size(); i++){
			if(events.get(i).needToDoNow(timeInMs)){
				executionList.add(events.get(i));
			}
		}
		
		
		/*
		 * Do executions
		 */
		boolean needToValidatePWMOutputs = false;
		for(int i=0; i<executionList.size(); i++){
			TimedOutputEvent event = executionList.get(i);
			if(event.getType() == OutputConfig.Type.PWM){
				needToValidatePWMOutputs = true;
				for(int j=0; j<event.getgetOutputStateCount(); j++){
					TaskExecutor.setPwmOutputValueWithoutSetAnyOutputYet((int)event.getOutputState(j).getPin(), (int)event.getOutputState(j).getValue());
				}
			}
			else
			if(executionList.get(i).getType() == OutputConfig.Type.IO){
				for(int j=0; j<event.getgetOutputStateCount(); j++){
					TaskExecutor.setIOState((int)event.getOutputState(j).getPin(), (event.getOutputState(j).getValue() > 0));
				}
			}
		}
		
		if(needToValidatePWMOutputs){
			TaskExecutor.validatePwmOutputs();
		}
		
	}

	@Override
	public boolean needToRun() {
		return this.isEnabled();
	}

}
