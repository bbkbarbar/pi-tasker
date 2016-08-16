package hu.barbar.tasker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;

import hu.barbar.comm.server.MultiThreadServer;
import hu.barbar.comm.util.BaseCommands;
import hu.barbar.comm.util.Msg;
import hu.barbar.comm.util.tasker.Commands;
import hu.barbar.comm.util.tasker.PWMMessage;
import hu.barbar.comm.util.tasker.RGBMessage;
import hu.barbar.tasker.log.EventLogger;
import hu.barbar.tasker.log.IOLogger;
import hu.barbar.tasker.todo.Worker;
import hu.barbar.tasker.todo.items.TempLogger2;
import hu.barbar.tasker.todo.items.TempLogger3;
import hu.barbar.tasker.todo.items.TempOnColors;
import hu.barbar.tasker.todo.items.TempWarning;
import hu.barbar.tasker.todo.items.WebUIUpdater;
import hu.barbar.tasker.todo.items.tempcontrol.CoolerController;
import hu.barbar.tasker.todo.items.tempcontrol.HeaterController;
import hu.barbar.tasker.todo.items.tempcontrol.TempController;
import hu.barbar.tasker.todo.items.util.TempReader;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Env;
import hu.barbar.tasker.util.GPIOHelper;
import hu.barbar.tasker.util.JSONHelper;
import hu.barbar.tasker.util.OutputConfig;
import hu.barbar.tasker.util.PWMOutputState;
import hu.barbar.util.FileHandler;
import hu.barbar.util.Mailer;
import hu.barbar.util.logger.Log;

public class Tasker {
	
	private static final int buildNum = 77;
	
	public static final boolean DEBUG_MODE = false;
	
	public static final int DEFAULT_PORT = (DEBUG_MODE? 10710 : 10713);
	
	protected static final String DEFAULT_DATETIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
	

	protected static Tasker me = null;
	
	private MultiThreadServer myServer = null;
	
	private Worker myWorker = null;
	
	protected static PWMOutputState pwmOutputStates = null;
	protected static int[] ioOutputStates = null;
	
	
	
	public static void main(String[] args) {
		
		int portArg = DEFAULT_PORT;
		if(args.length>1){
			try{
				portArg = Integer.valueOf(args[1].trim());
			}catch(Exception e){
				portArg = DEFAULT_PORT;
			}
		}
		
		me = new Tasker();
		me.startApp(portArg);
		
	}
	
	private void startApp(int port){
		
		System.out.println ("Start tasker ("
				+ getTimeStamp(new Date())
				+ ")\nBuild: " + buildNum + "\n");/**/
		
		
		Log.init(Env.getDataFolderPath() + "logs/", Log.Level.DEBUG, Log.Level.WARN);
		Log.f("Start tasker ("
				+ ")\nBuild: " + buildNum + "\n");
		
		/*
		 *  Load previous states of PWM outputs
		 */
		Tasker.pwmOutputStates = new PWMOutputState();
		Tasker.pwmOutputStates.setValues(PWMOutputState.loadContentFromFile());
		
		
		/*
		 *  Load previous states of IO outputs
		 */
		//TODO!!!!!!
		Tasker.ioOutputStates = new int[GPIOHelper.MAX_PIN_NUMBER];
		
		Mailer.readConfig();
		
		Log.info("Mailer initialized.\n");
		
		EventLogger.initialize();
		
		IOLogger.initialize();
		
		/*
		 * Set PWM outputs according to the saved states 
		 */
		Log.d("Load pwm outputs from file..");
		TaskExecutor.setAllPwmOutputs(Tasker.pwmOutputStates.getValues(), false);
		
		
		
		Log.d("Start server..");
		myServer = new MultiThreadServer(port){
			@Override
			protected boolean handleInput(Msg msg, Long clientId) {
				return Tasker.me.handleReceivedMessage(msg, clientId);
			}

			@Override
			protected void onClientExit(Long clientId) {
			}
			
		};
		myServer.start();
		EventLogger.add("Server started @ port " + port);
		Log.d("Server started.");

		
		if(!DEBUG_MODE){
			Worker logWorker = new Worker("Temp logger", 120);
			TempLogger2 tl = new TempLogger2();
			tl.setValidityOfMeteringResult(15);
			logWorker.addToDoItem(tl);
			
			TempLogger3 tlWithCoolerState = new TempLogger3(Config.readOutputConfig(false).get(Config.KEY_OUTPUT_OF_COOLER));
			tlWithCoolerState.setValidityOfMeteringResult(15);
			logWorker.addToDoItem(tlWithCoolerState);
					
			logWorker.start();
		
		
			myWorker = new Worker(30);
		
			TempOnColors toc = new TempOnColors();
			toc.setServer(myServer);
			toc.setValidityOfMeteringResult(10);
			toc.setEnabled(false);
			myWorker.addToDoItem(toc);
		
			WebUIUpdater webupdater = new WebUIUpdater();
			webupdater.setValidityOfMeteringResult(15);
			myWorker.addToDoItem(webupdater);
		
			/*
			TempChange tc = new TempChange("Temperature diff > 0.15C", 0.15f);
			tc.setValidityOfMeteringResult(10);
			myWorker.addToDoItem(tc);
			/**/
			
			/*TempBasedControl tbc = new TempBasedControl(TempReader.SENSOR_WATER, "Water too hot (>25.75)", 25.75f, TempWarning.WARMER, 0.5f, Config.readOutputConfig(true).get(Config.KEY_OUTPUT_OF_COOLER).getPin(), true);
			myWorker.addToDoItem(tbc);
			/**/
			
			/*
			ArrayList<TempController.RuleItem> rules = new ArrayList<TempController.RuleItem>();
			TempController.RuleItem ri = new TempController.RuleItem(25.70f, 0.25f, 32);
			//FileHandler.storeJSON( Env.getDataFolderPath() + "test.json", ri.getAsJSON());
			
			//JSONObject json = FileHandler.readJSON( Env.getDataFolderPath() + "test.json" );
			
			rules.add( ri ); 
					
			rules.add( new TempController.RuleItem(28.00f, 0.3f, 80) );
			rules.add( new TempController.RuleItem(26.00f, 0.1f, 40) );
			
			
			//CoolerController cc = new CoolerController(Config.readOutputConfig(true).get(Config.KEY_OUTPUT_OF_COOLER), "Aquarium cooler", TempReader.SENSOR_WATER, rules);
			JSONObject ccJson = FileHandler.readJSON(Env.getDataFolderPath() + "cc3.json");
			if(ccJson != null){
				CoolerController cc = new CoolerController(Config.readOutputConfig(true).get(Config.KEY_OUTPUT_OF_COOLER), ccJson);
				cc.setEnabled(false);
	
				//JSONObject ccJson = cc.getAsJSON();
				//FileHandler.storeJSON(Env.getDataFolderPath() + "cc2.json", ccJson);
				
				myWorker.addToDoItem(cc);
			}else{
				Log.w("CoolerController can not be initialited because can not read \"cc3.json\".");
			}
			
			
			ArrayList<TempController.RuleItem> rules2 = new ArrayList<TempController.RuleItem>();
			rules2.add( new TempController.RuleItem(25.70f, 0.10f, 12) );
			rules2.add( new TempController.RuleItem(28.00f, 0.05f, 80) );
			rules2.add( new TempController.RuleItem(26.00f, 0.1f, 30) );
			rules2.add( new TempController.RuleItem(26.20f, 0.1f, 40) );
			CoolerController cc2 = new CoolerController(Config.readOutputConfig(true).get(Config.KEY_OUTPUT_OF_COOLER), "Aquarium cooler", TempReader.SENSOR_WATER, rules2);
			cc2.setEnabled(true);
			myWorker.addToDoItem(cc2);
			JSONObject cc2Json = cc2.getAsJSON();
			FileHandler.storeJSON(Env.getDataFolderPath() + "coolerController.json", cc2Json);
			/**/
			
			JSONObject json = FileHandler.readJSON(Env.getDataFolderPath() + "coolerController.json");
			if(JSONHelper.matchingObjectType(json, "CoolerController") > 0){
				CoolerController cc = new CoolerController(Config.readOutputConfig(false).get(Config.KEY_OUTPUT_OF_COOLER), json);
				myWorker.addToDoItem(cc);
			}
			
			
			
			/*
			ArrayList<TempController.RuleItem> rulesForHeaterController = new ArrayList<TempController.RuleItem>();
			rulesForHeaterController.add( new TempController.RuleItem(24.80f, 0.10f, 100) );
			HeaterController hc = new HeaterController(Config.readOutputConfig(false).get(Config.KEY_OUTPUT_OF_HEATER), "Aquarium heater", TempReader.SENSOR_WATER, rulesForHeaterController);
			hc.setEnabled(true);
			/**/
			json = FileHandler.readJSON(Env.getDataFolderPath() + "heaterController.json");
			if(JSONHelper.matchingObjectType(json, "HeaterController") > 0){
				HeaterController hc = new HeaterController(Config.readOutputConfig(false).get(Config.KEY_OUTPUT_OF_HEATER), json);
				myWorker.addToDoItem(hc);
			}
			
			
			TempWarning ate = new TempWarning(TempReader.SENSOR_AIR, "Air temperature > 30.0C", 30.0f, TempWarning.DIRECTION_INCREASING, 1.0f);
			myWorker.addToDoItem(ate);
			
			TempWarning wte = new TempWarning(TempReader.SENSOR_WATER, "Water temperature < 24.0C", 24.0f, TempWarning.COLDER, 0.25f);
			wte.addRecipient("bbk.barbar@gmail.com");
			//wte.addRecipient("baboshenrietta@gmail.com");
			myWorker.addToDoItem(wte);
			
			/*
			TempWarning wth = new TempWarning(TempReader.SENSOR_WATER, "Water temperature > 27.75C", 27.75f, TempWarning.WARMER, 0.25f);
			wth.addRecipient("bbk.barbar@gmail.com");
			wth.addRecipient("baboshenrietta@gmail.com");
			FileHandler.storeJSON(Env.getDataFolderPath() + "TempWarningSample.json", wth.getAsJSON());	//TODO: testing only
			/**/
			JSONObject twJson = FileHandler.readJSON(Env.getDataFolderPath() + "TempWarningSample.json");
			if(twJson != null){
				TempWarning wth = new TempWarning(twJson);
				myWorker.addToDoItem(wth);
			}else{
				Log.w("TempWarning can not be initialited because can not read \"TempWarningSample.json\".");
			}
	
			myWorker.start();
		}
		
		/*
		Worker myWorker2 = new Worker("Check anyting one per an hour", 3600);
		TempExceeds te2 = new TempExceeds("Temperature < 15.0C", 15f, TempExceeds.DIRECTION_DECREASING, 1.0f);
		myWorker2.addToDoItem(te2);
		myWorker2.start();
		/**/
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		
		System.out.println("\n==============\n");
		Log.i(getWorkerInfos());
		System.out.println("\n==============\n");
		
		
	}
	
	
	protected String getWorkerInfos(){
		ArrayList<Worker> workers = Worker.getWorkerList();
		String content = "Worker count: " + workers.size();
		for(int i=0; i<workers.size(); i++){
			content += "\n" + workers.get(i).toString("\n"); 
		}
		return content;
	}
	
	private static String getTimeStamp(Date d){
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
		return sdf.format(d);
	}
	
	protected boolean handleReceivedMessage(Msg msg, Long clientId){
		
		if( msg.getContent().startsWith(Commands.GET_TEMP)){
			String response = TaskExecutor.readTemp("not_used_yet");
			myServer.sendToClient(new Msg("Temp: " + response, Msg.Types.PLAIN_TEXT), clientId);
		}
		else
		
		if(msg.getType() == Msg.Types.PWM_COMMAND){
			PWMMessage pwmM = (PWMMessage)msg;
			pwmOutputStates.setValue(pwmM.getChannelID(), (int)(pwmM.getValue()*40.95f) );
			TaskExecutor.setAllPwmOutputs(pwmOutputStates.getValues(), true);
			
		}else
		
		if(msg.getType() == Msg.Types.RGB_COMMAND){
			RGBMessage rgbMsg = (RGBMessage)msg;
			Log.d("Color received from client [" + clientId + "]: " + rgbMsg.toString());
			TaskExecutor.setColor(rgbMsg.getRed(), rgbMsg.getGreen(), rgbMsg.getBlue());
			myServer.sendToClient(new Msg("New color accepted.", Msg.Types.PLAIN_TEXT), clientId);
			
		}else
		
		if(msg.getType() == Msg.Types.REQUEST){
			
			if( msg.getContent().startsWith(Commands.GET_CPU_TEMP) ){
				
				String response = TaskExecutor.readCPUTemp();
				myServer.sendToClient(new Msg(response, Msg.Types.RESPONSE_CPU_TEMP), clientId);
			}
			
			if( msg.getContent().startsWith(Commands.SEND_INFO_MAIL) ){
				
				// content should looks like: "sendinfomail recipiant@gmail.com"
				String recipiant = msg.getContent().split(" ")[1];
				
				boolean emailSent = false;
				
				if(recipiant != null && recipiant.contains("@")){
					//TODO: !!! create content for info mail !!!
					 emailSent = Mailer.sendEmail(
											recipiant,
											"Info",
											"content"
					);
				}
				
				String response = " ";
				if(emailSent){
					response = "Info mail sent to: " + recipiant;
				}else{
					response = "Info mail CAN NOT BE sent to: " + recipiant;
				}
				myServer.sendToClient(new Msg(response, Msg.Types.PLAIN_TEXT), clientId);
			}
			
			else
			if( msg.getContent().startsWith(Commands.GET_WORKER_INFO) ){
				String response = getWorkerInfos();
				myServer.sendToClient(new Msg(response, Msg.Types.RESPONSE_WORKER_INFO), clientId);
			}
	
			else
			if( msg.getContent().startsWith(Commands.SET_PWM_OUTPUT) ){
				
				String[] parts = msg.getContent().split(" ");
				if (parts.length < 3){
					String response = "Invalid io command: " + msg.getContent();
					//System.out.println(response);
					myServer.sendToClient(new Msg(response, Msg.Types.PLAIN_TEXT), clientId);
				}else{
					try{
						int channel = Integer.valueOf(parts[1]);
						int value = Integer.valueOf(parts[2]);
						
						pwmOutputStates.setValue(channel, value );
						TaskExecutor.setAllPwmOutputs(pwmOutputStates.getValues(), true);
						
						EventLogger.add("Command from client: SetPWM channel: " + channel + " value: " + value);
						
						myServer.sendToClient(new Msg("SetPWM: DONE (ch" + channel + ": " + value + ")", Msg.Types.PLAIN_TEXT), clientId);
						
					}catch(Exception e){
						myServer.sendToClient(new Msg("SetPWM: ERROR", Msg.Types.PLAIN_TEXT), clientId);
					}
				}
			} 

			/*
			 *  Send current PWM states to client
			 */
			else
			if( msg.getContent().startsWith(Commands.GET_PWM_OUTPUT_VALUES) ){
				
				String response = pwmOutputStates.getContentAsSingleLine();
				myServer.sendToClient(new Msg(response, Msg.Types.PWM_OUTPUT_STATES), clientId);
				
			}
			
			else
			if( msg.getContent().startsWith(Commands.SET_IO_OUTPUT) ){
				
				String[] parts = msg.getContent().split(" ");
				if (parts.length < 3){
					String response = "Invalid io command: " + msg.getContent();
					//System.out.println(response);
					myServer.sendToClient(new Msg(response, Msg.Types.PLAIN_TEXT), clientId);
				}else{
					try{
						boolean state = (parts[2].equals("0"))?false:true;
						int pinNum = Integer.valueOf(parts[1]);
						
						TaskExecutor.setIOState(pinNum, state);
						EventLogger.add("Command from client: SetIO pin: " + pinNum + " " + (state?"1":"0"));
						
						myServer.sendToClient(new Msg("SetIO: DONE", Msg.Types.PLAIN_TEXT), clientId);
						
					}catch(Exception e){
						myServer.sendToClient(new Msg("SetIO: ERROR", Msg.Types.PLAIN_TEXT), clientId);
					}
				}

			}

			else
			if( (msg.getContent().startsWith(Commands.ENABLE_TODO_ITEM)) || (msg.getContent().startsWith(Commands.DISBALE_TODO_ITEM)) ){
				
				String[] input = msg.getContent().split(" ");
				ArrayList<Integer> ids = new ArrayList<Integer>();
				for(int i=1; i<input.length; i++){
					try{
						ids.add(Integer.valueOf(input[i]));
					}catch(Exception doesNotMatterIfNumberFormatExceptionThrown){}
				}
				
				boolean newState = false;
				if(msg.getContent().startsWith(Commands.ENABLE_TODO_ITEM)){
					newState = true;
				}
				
				/*
				 * Set newState for items with given ids..
				 */
				int modifiedItemCount = 0;
				ArrayList<Worker> workerList = Worker.getWorkerList();
				for(int w=0; w<workerList.size(); w++){
					modifiedItemCount += workerList.get(w).setStateForItems(ids, newState);
				}
				
				myServer.sendToClient(new Msg("Modified item count: " + modifiedItemCount,
											  Msg.Types.PLAIN_TEXT), 
						              clientId);
				
			}
		
			if( msg.getContent().startsWith(Commands.EMAIL_WORKER_INFO) ){
				
				String recipiant = msg.getContent().split(" ")[1];
				boolean emailSent = false;
				if(recipiant != null && recipiant.contains("@")){
					
					String content = getWorkerInfos();
					emailSent = Mailer.sendEmail(
										recipiant,
										"Worker info",
										content
					);
				}
				
				String response = " ";
				if(emailSent){
					response = "Worker info mail sent to: " + recipiant;
				}else{
					response = "Worker info mail CAN NOT BE sent to: " + recipiant;
				}
				myServer.sendToClient(new Msg(response, Msg.Types.PLAIN_TEXT), clientId);
			}
		}
		
		else
		if( msg.getContent().startsWith(Commands.GET_DATE)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			Date now = new Date();
			String dateStr = sdf.format(now);
			myServer.sendToClient(new Msg(dateStr, Msg.Types.PLAIN_TEXT), clientId);
		}

		else
		if( msg.getContent().startsWith(BaseCommands.GET_CLIENT_COUNT)){
			String response = Integer.toString(myServer.getActiveClientCount());
			myServer.sendToClient(new Msg("Client count: " + response, Msg.Types.PLAIN_TEXT), clientId);
		}

		/*
		else
		if( msg.getContent().contains("getClientList")){
			System.out.println("Client wants to get current client list.");
			String response = myServer.getClientList();
			myServer.sendToClient(new Msg("Clients: " + response, Msg.Types.PLAIN_TEXT), clientId);
			System.out.println("ClientList sent to client:\n" + response);
		}/**/
			
		Log.d("Message received from client [" + clientId + "]: " + msg.toString());
		
		return false;
	}

	
	/**
	 * @param outputConfigOfCooler
	 * @return 1 || 0 if output type is IO or <br>
	 *         the decimal value in 12bit if output type is PWM or <br>
	 *         OutputConfig.INVALID (-2) if outputConfig type of instance is NOT equals Type.PWM or Type.IO,
	 */
	public static int getOutputState(OutputConfig outputConfig) {
		if(outputConfig.getType() == OutputConfig.Type.PWM){
			return Tasker.pwmOutputStates.getValue(outputConfig.getPin());
		}else
		if(outputConfig.getType() == OutputConfig.Type.IO){
			return Tasker.ioOutputStates[outputConfig.getPin()];
		}
		else{
			return OutputConfig.INVALID;
		}
	}

}
