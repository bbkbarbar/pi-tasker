package hu.barbar.tasker.util.usagelog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EnergyConsumptionInfo {
	
	public static final float ACTIVE_TIME_UNDEFINED = -3f;

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss";

	
	private String dateFormatPattern = DEFAULT_DATE_FORMAT_PATTERN;
	
	private Date startDate = null;
	
	private float activeTimeInHours = ACTIVE_TIME_UNDEFINED;
	
	private float energyConsumption = 0;

	
	public EnergyConsumptionInfo(Date startDate, float activeTimeInHours, float energyConsumption) {
		super();
		this.startDate = startDate;
		this.activeTimeInHours = activeTimeInHours;
		this.energyConsumption = energyConsumption;
	}

	
	public String toString(){
		SimpleDateFormat format = new SimpleDateFormat(dateFormatPattern);
        String dateStr = format.format(this.startDate);
        String wattHourValueStr = String.format("%.3f", getWattHours());
		return wattHourValueStr + " Wh since " + dateStr;
	}
	
	public float getWattHours(){
		return this.energyConsumption * this.activeTimeInHours;
	}
	
}
