package hu.barbar.tasker.todo.items;

import hu.barbar.comm.server.MultiThreadServer;
import hu.barbar.comm.util.Msg;
import hu.barbar.tasker.TaskExecutor;
import hu.barbar.tasker.todo.items.util.TempRelatedToDoItemBase;
import hu.barbar.tasker.util.TemperatureResult;
import hu.barbar.util.logger.Log;

public class TempOnColors extends TempRelatedToDoItemBase {
	
	private MultiThreadServer myServer = null;
	
	//TaskExecutor.setColor(rgbMsg.getRed(), rgbMsg.getGreen(), rgbMsg.getBlue());
	
	@Override
	public String getClassTitle() {
		return "Show temp on colors";
	}
	
	public void setServer(MultiThreadServer server){
		this.myServer = server;
	}

	@Override
	public void execute() {
		TemperatureResult temp = readTemperature();
		
		if( TemperatureResult.isValueInvalid(temp.getTempOfAir()) ){
			Log.w("TempOnColors (ToDoItem: "
					+ getId()
					+ ") Invalid temp value has been read: " + temp.getTempOfAir());
			return;
		}
		
		Log.d("TemoOnColor :: Temp: " + String.format("%.2f", temp.getTempOfAir()) + "'C");
		if(temp.hasValue()){
			
			if(temp.getTempOfAir() < 27f){
				TaskExecutor.setColor(0, 0, 255);
			}else
			if(temp.getTempOfAir() < 28){
				TaskExecutor.setColor(0, 255, 0);
			}else
			if(temp.getTempOfAir() < 29){
				TaskExecutor.setColor(255, 100, 0);
			}else{
				TaskExecutor.setColor(255, 0, 0);
			}
			
			if(myServer != null){
				myServer.sendToClient(new Msg("Temp: " + String.format("%.2f", temp.getTempOfAir()) + "'C"), MultiThreadServer.ID_FOR_ALL_CLIENT);
			}
			
		}
	}

	@Override
	public boolean needToRun() {
		return true;
	}

}
