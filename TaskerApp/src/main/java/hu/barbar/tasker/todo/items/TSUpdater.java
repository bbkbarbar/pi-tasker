package hu.barbar.tasker.todo.items;

import com.angryelectron.thingspeak.Channel;
import com.angryelectron.thingspeak.Entry;
import com.angryelectron.thingspeak.ThingSpeakException;
import com.mashape.unirest.http.exceptions.UnirestException;
import hu.barbar.tasker.todo.items.util.CalculatedField;
import hu.barbar.tasker.todo.items.util.IoTDataReader;
import hu.barbar.tasker.util.CommonHelper;
import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Defaults;
import hu.barbar.util.Pair;
import hu.barbar.util.logger.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class TSUpdater extends ToDoItemBase {

    protected String name = "Unnamed ThingSpeak channel";
    protected int channelId = Defaults.THINGSPEAK_CHANNEL_ID_UNDEFINED;
    protected String apiWriteKey = "UNDEFINED";

    protected ArrayList<IoTDeviceHandler> ioTDeviceHandlers;


    protected class IoTDeviceHandler {

        protected String iotUrl = "undefinedIp";
        protected String iotPath = "/undefined";

        protected  String name = "";

        protected ArrayList<Pair<Integer, String>> fields;
        protected ArrayList<CalculatedField> calculatedFields;


        public IoTDeviceHandler(JSONObject json) {

            if( (!json.containsKey("url")) ||
                (!json.containsKey("path")) ||
                (!json.containsKey("fields"))
            ){
                Log.w("TSUpdater -> Config should contains JSON object under \"thingSpeak.<name>.IoTDevices\" with the following keys: url, path, fields ");
                return;
            }

            this.name = (String) json.get("name");

            this.iotUrl = (String) json.get("url");
            this.iotPath = (String) json.get("path");

            /*
                Add fields
             */
            this.fields = new ArrayList<>();
            JSONArray fieldsArr = (JSONArray) json.get("fields");
            for(int i=0; i<fieldsArr.size(); i++){
                JSONObject fieldJson = (JSONObject) fieldsArr.get(i);
                Integer fieldId = ((Long)fieldJson.get("fieldId")).intValue();
                String valueName = (String) fieldJson.get("value_name");
                fields.add(new Pair(fieldId, valueName));
            }

            /*
                Add calculated fields
             */
            calculatedFields = new ArrayList<>();
            if(json.containsKey("calculated fields")){

                JSONArray calculatedFieldsJsonArr = (JSONArray) json.get("calculated fields");
                //System.out.println("\n\n" + calculatedFieldsJsonArr.toJSONString() + "\n");
                for(int i=0; i<calculatedFieldsJsonArr.size(); i++){
                    JSONObject calculatedFieldJson = (JSONObject) calculatedFieldsJsonArr.get(i);
                    CalculatedField cf = new CalculatedField(calculatedFieldJson);
                    this.calculatedFields.add(cf);
                }


                //TODO itt
            }

        }

        public String toString(){
            return this.toString("");
        }

        public String toString(String prefix){
            String fieldsStr = "";
            for(int i=0; i<this.fields.size(); i++){
                fieldsStr += prefix + "\t" + this.fields.get(i).getKey() + " -> " + this.fields.get(i).getValue() + "\n";
            }
            return prefix + "Name: " + this.name + "\n" +
                    prefix + "Url: " + this.iotUrl + "\n" +
                    prefix + "Path: " + this.iotPath + "\n" +
                    prefix + "fields:\n" + fieldsStr;
        }

        public String getUrl() {
            return this.iotUrl;
        }

        public String getPath(){
            return this.iotPath;
        }

        public ArrayList<Pair<Integer, String>> getFields() {
            return this.fields;
        }
    }



    public TSUpdater(String name) {
        
    	this.enabled = true;
    	
    	this.name = name;

        this.channelId = Config.getInt("thingSpeak." + this.name + ".channelId", -1);
        this.apiWriteKey = Config.getString("thingSpeak." + this.name + ".write api key", "UNDEFINED_API_KEY");

        //this.iotUrl = Config.getString("thingSpeak." + tsChannelName + ".IoT url", "UNDEFINED_URL_FOR_IOT_DEVICE");
        //this.iotPath = Config.getString("thingSpeak." + tsChannelName + ".IoT path", "UNDEFINED_PATH_FOR_IOT_DEVICE");

        this.ioTDeviceHandlers = new ArrayList<>();
        JSONArray deviceList = Config.getJSONArray("thingSpeak." + this.name + ".IoT devices");

        for(int i=0; i<deviceList.size(); i++){
            JSONObject iotDeviceJson = (JSONObject) deviceList.get(i);
            IoTDeviceHandler ioTDeviceHandler = new IoTDeviceHandler(iotDeviceJson);
            this.ioTDeviceHandlers.add(ioTDeviceHandler);

            //System.out.println("\nIoTDeviceHander added:\n" + ioTDeviceHandler.toString());
        }

        Log.i("ThingSpeak Updater initialzed:");
        Log.i("\tName: " + this.name);
        Log.i("\tChannelId: " + this.channelId);
        Log.i("\tDevices: ");
        for(int i=0; i<this.ioTDeviceHandlers.size(); i++){
            Log.i(this.ioTDeviceHandlers.get(i).toString("\t\t"));
        }

        //System.out.print("\n\n\n====================\n" + deviceList);


    }

    @Override
    public void execute() {
        Channel channel = null;
        Entry entry = new Entry();
        try {
            for(int i=0; i<this.ioTDeviceHandlers.size(); i++){
                JSONObject values = (JSONObject) IoTDataReader.readJSONDataFromIoTDevice(ioTDeviceHandlers.get(i).getUrl(), ioTDeviceHandlers.get(i).getPath()).get("values");

                // handle normal fields
                for(int f=0; f < this.ioTDeviceHandlers.get(i).getFields().size(); f++){
                    Pair<Integer, String> pair = ioTDeviceHandlers.get(i).getFields().get(f);
                    if(values.containsKey(pair.getValue())){
                        Integer fieldId = pair.getKey();
                        //System.out.println("Try to add value as: fieldId: " + fieldId + " ValueJsonKey: " + pair.getValue());
                        entry.setField(fieldId, (String) values.get(pair.getValue()));
                        //System.out.println("Update:\n\tFieldId: " + fieldId + "\n\tValue: " + (String) values.get(pair.getValue()));
                    }
                }

                // handle calculated fields
                for(int c=0; c < this.ioTDeviceHandlers.get(i).calculatedFields.size(); c++){
                    double calculatedVaporContent = this.ioTDeviceHandlers.get(i).calculatedFields.get(c).getCalculatedValue(values);
                    calculatedVaporContent = CommonHelper.round(calculatedVaporContent, 2);
                    int fieldIdForTS = this.ioTDeviceHandlers.get(i).calculatedFields.get(c).getFieldId();
                    //System.out.println("\n\nCalculated:\n\tFieldId: " + fieldIdForTS + "\n\tValue: " + calculatedVaporContent + "g/m3");
                    entry.setField(fieldIdForTS, "" + calculatedVaporContent);
                    //System.out.println("Update:\n\tFieldId: " + fieldIdForTS + "\n\tValue: " + calculatedVaporContent);
                }

            }

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
            Log.d("TS update successful!");
        }
    }

    @Override
    public String getClassTitle() {
        return "IoT data dispatcher for " + this.name;
    }

    @Override
    public boolean needToRun() {
        return this.isEnabled();
    }
}
