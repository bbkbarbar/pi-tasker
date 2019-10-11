package hu.barbar.tasker.todo.items;

import com.angryelectron.thingspeak.Channel;
import com.angryelectron.thingspeak.Entry;
import com.angryelectron.thingspeak.ThingSpeakException;
import com.mashape.unirest.http.exceptions.UnirestException;
import hu.barbar.tasker.todo.items.util.IoTDataReader;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Defaults;
import hu.barbar.util.logger.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class IoTDataDispatcher extends IoTDataReader {

	protected int THINGSPEAK_CHANNEL_ID_OF_DTH_TEST_CHANNEL = 876121;

	protected String tsChannelName = "Unnamed ThingSpeak channel";
	protected int channelId = Defaults.THINGSPEAK_CHANNEL_ID_UNDEFINED;
	protected String apiWriteKey = "UNDEFINED";

	//protected JSONArray fields = null;


	@Override
	public String getClassTitle() {
		return "IoT data dispatcher for " + this.tsChannelName;
	}
	
	public IoTDataDispatcher(String TSChannelName) {
		super();

		this.tsChannelName = TSChannelName;
		this.channelId = Config.getInt("thingSpeak." + tsChannelName + ".channelId", THINGSPEAK_CHANNEL_ID_OF_DTH_TEST_CHANNEL);
		this.apiWriteKey = Config.getString("thingSpeak." + tsChannelName + ".write api key", "UNDEFINED_API_KEY");

		this.iotUrl =  Config.getString("thingSpeak." + tsChannelName + ".IoT url", "UNDEFINED_URL_FOR_IOT_DEVICE");
		this.iotPath = Config.getString("thingSpeak." + tsChannelName + ".IoT path", "UNDEFINED_PATH_FOR_IOT_DEVICE");

		Log.i("ThingSpeak updater initialzed:");
		Log.i("\tName: " + this.tsChannelName);
		Log.i("\tChannelId: " + this.channelId);
		Log.i("\tIoT url: " + this.iotUrl);
		Log.i("\tIoT path: " + this.iotPath);
		/*
		System.out.println("TS channel name: " + TSChannelName);
		System.out.println("TS channelID: " + this.channelId);
		System.out.println("TS apiWriteKey: " + this.apiWriteKey);
		/**/

		//fields = Config.getJSONArray("thingSpeak." + tsChannelName + ".fields");

		// TODO initialize this function
	}

	@Override
	public void execute() {
		Channel channel = null;
		Entry entry = new Entry();
		try {
			JSONObject values = (JSONObject) this.readJSONDataFromIoTDevice().get("values");
			//System.out.println(values.toJSONString());


			if(values.containsKey("tempC")){
				//Log.i("Homerseklet: " + values.get("tempC"));
				entry.setField(3, (String) values.get("tempC"));
			}
			if(values.containsKey("humidity")) {
				//Log.i("Paratartalom: " + values.get("humidity"));
				entry.setField(2, (String) values.get("humidity"));
			}
			if(values.containsKey("heatIndex")) {
				//Log.i("Hoerzet: " + values.get("heatIndex"));
				entry.setField(1, (String) values.get("heatIndex"));
			}
			/**/

			//Entry entry = new Entry();
			/*
			entry.setField(1, (String) values.get("heatIndex"));
			entry.setField(2, (String) values.get("humidity"));
			entry.setField(3, (String) values.get("tempC"));
			/**/
			/*
			if(values.containsKey("heatIndex")) {
				String s = (String) values.get("heatIndex");
				if(s.equalsIgnoreCase("nan")){

				}else {
					entry.setField(1, s);
				}
			}
			if(values.containsKey("humidity")) {
				String s = (String) values.get("humidity");
				if(s.equalsIgnoreCase("nan")){

				}else {
					entry.setField(2, s);
				}
			}
			if(values.containsKey("tempC")) {
				String s = (String) values.get("tempC");
				if(s.equalsIgnoreCase("nan")){

				}else {
					entry.setField(3, s);
				}
			}
			/**/

		}catch (ParseException e) {
			Log.w("Parse exception occured:");
			Log.w(e.toString());
			e.printStackTrace();
		}catch (Exception exceptionToHandle){
			//TODO ITT
			Log.w("Exception:");
			Log.w(exceptionToHandle.toString());
			exceptionToHandle.printStackTrace();
		}


		boolean tsUpdateDone = false;
		try {
			// ============ ThingSpeak ============
			channel = new Channel(channelId, apiWriteKey);

			channel.update(entry);
			tsUpdateDone = true;
		} catch (ThingSpeakException | UnirestException | NullPointerException e) { //
			tsUpdateDone = false;
			Log.w(getClassTitle() + "Channel update exception");
			Log.w(e.toString());
		}
		if(tsUpdateDone){
			Log.i("TS update successful!");
		}
	}

	@Override
	public boolean needToRun() {
		return this.isEnabled();
	}

}
