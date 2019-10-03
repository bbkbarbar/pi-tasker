package hu.barbar.tasker.todo.items;

import com.angryelectron.thingspeak.Channel;
import com.angryelectron.thingspeak.Entry;
import com.angryelectron.thingspeak.ThingSpeakException;
import com.mashape.unirest.http.exceptions.UnirestException;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Defaults;
import hu.barbar.util.logger.Log;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class IoTDataDispatcher extends ToDoItemBase {

	protected int THINGSPEAK_CHANNEL_ID_OF_DTH_TEST_CHANNEL = 876121;

	protected String tsChannelName = "Unnamed ThingSpeak channel";
	protected int channelId = Defaults.THINGSPEAK_CHANNEL_ID_UNDEFINED;
	protected String apiWriteKey = "UNDEFINED";
	protected String iotUrl = "undefined";
	protected String iotPath = "/undefined";

	protected JSONArray fields = null;


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
		/*
		System.out.println("TS channel name: " + TSChannelName);
		System.out.println("TS channelID: " + this.channelId);
		System.out.println("TS apiWriteKey: " + this.apiWriteKey);
		/**/

		fields = Config.getJSONArray("thingSpeak." + tsChannelName + ".fields");

		// TODO initialize this function
	}

	@Override
	public void execute() {
		RestAssured.baseURI = this.iotUrl;

		// request object
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.GET, this.iotPath );

		JSONParser parser = new JSONParser();
		JSONObject j = null;
		try {
			j = (JSONObject) parser.parse(response.getBody().asString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//System.out.println(response.getBody().asString());

		JSONObject values = (JSONObject) j.get("values");

		System.out.println("Homerseklet: " +  values.get("tempC"));
		System.out.println("Paratartalom: " + values.get("humidity"));
		System.out.println("Hőérzet: " + values.get("heatIndex"));
		/**/

		// ============ ThingSpeak ============
		Channel channel = new Channel(channelId, apiWriteKey);

		Entry entry = new Entry();

		/*
		for(int i=0; i<fields.size(); i++){
			JSONObject jo = (JSONObject) fields.get(i);
			//int id = (int) jo.get("fieldId");
			entry.setField((int) jo.get("fieldId") , (String) values.get((String)jo.get("value_name")));
			//System.out.println("FieldId: " + jo.get("fieldId") + "\t" + jo.get("value_name"));
		}
		/**/
		entry.setField(1, (String) values.get("heatIndex"));
		entry.setField(2, (String) values.get("humidity"));
		entry.setField(3, (String) values.get("tempC"));

		boolean tsUpdateDone = false;
		try {
			channel.update(entry);
			tsUpdateDone = true;
		} catch (ThingSpeakException | UnirestException e) { //
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
