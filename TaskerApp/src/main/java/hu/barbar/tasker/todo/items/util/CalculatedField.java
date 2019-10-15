package hu.barbar.tasker.todo.items.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import hu.barbar.util.logger.Log;

import java.util.ArrayList;

public class CalculatedField {

    protected ArrayList<String> calculatedFrom = null;
    protected String name = null;
    protected Integer fieldId = -1;

    public CalculatedField(JSONObject json) {

        if( (!json.containsKey("from")) ||
            (!json.containsKey("name")) ||
            (!json.containsKey("fieldId"))
        ){
            Log.w("CalculatedField -> Config should contains JSON object under \"thingSpeak.<name>.IoTDevices.<any_device>.calculated fields\" with the following keys: name, from, fieldId ");
            return;
        }

        JSONArray fromKeyArr = (JSONArray) json.get("from");
        calculatedFrom = new ArrayList<>();
        for(int i=0; i<fromKeyArr.size(); i++){
            JSONObject fromJsonObj = (JSONObject) fromKeyArr.get(i);
            calculatedFrom.add((String) fromJsonObj.get("value_name"));
        }
        this.name = (String) json.get("name");
        this.fieldId = ((Long)json.get("fieldId")).intValue();

    }

    public Double getCalculatedValue(JSONObject valuesJsonFromIoT){
        if(this.name.equalsIgnoreCase("air_density")){
            double tempC = Double.valueOf(String.valueOf(valuesJsonFromIoT.get(calculatedFrom.get(0))));
            double relativeHumidity = Double.valueOf(String.valueOf(valuesJsonFromIoT.get(calculatedFrom.get(1))));
            double fullDensity = calculateMaxVaporDensityForTemperature(tempC);
            double airVaporContentInGpCM = fullDensity * (relativeHumidity/100f);
            return airVaporContentInGpCM;
        }
        return null; // TODO handle this exception
    }

    /**
     * Calculate max vapor density for given temperature (°C).
     * @param tempC temperature in °C
     * @return vapor density in g/m3
     */
    public static Double calculateMaxVaporDensityForTemperature(Double tempC){
        return new Double(5.018 + (0.32321 * tempC) + (0.0081847*(tempC*tempC)) + (0.00031243*(tempC*tempC*tempC)) );
    }

    public int getFieldId() {
        return this.fieldId;
    }
}
