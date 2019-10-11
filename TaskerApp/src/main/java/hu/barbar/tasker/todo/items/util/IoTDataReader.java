package hu.barbar.tasker.todo.items.util;

import hu.barbar.tasker.todo.items.ToDoItemBase;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract  class IoTDataReader extends ToDoItemBase {

    protected String iotUrl = "undefinedIp";
    protected String iotPath = "/undefined";

    protected JSONObject readJSONDataFromIoTDevice() throws ParseException {
        RestAssured.baseURI = this.iotUrl;

        // request object
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.request(Method.GET, this.iotPath);

        JSONParser parser = new JSONParser();
        JSONObject j = (JSONObject) parser.parse(response.getBody().asString());
        return j;
    }

    protected String readStringValueFromJson(JSONObject values, String key, String defaultValue){
        if(values.containsKey(key)){
            String str = (String) values.get(key);
            if(str == null){
                return defaultValue;
            }else{
                return str;
            }
        }
        return defaultValue;
    }

    protected String readStringValueFromJson(JSONObject values, String key){
        return this.readStringValueFromJson(values, key, null);
    }


}
