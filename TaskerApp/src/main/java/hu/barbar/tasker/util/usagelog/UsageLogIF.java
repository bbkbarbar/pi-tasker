package hu.barbar.tasker.util.usagelog;

import java.util.Date;

public interface UsageLogIF {

	public Date getStartDate();
	
	public void add(UsageLogItem item);
	
	public void addNewLogItem();
	
	/**
	 * Get count of items.
	 * @return 
	 */
	public int getItemCount();
	
	/**
	 * Get count of items since specified begin date.
	 * @param begin
	 * @return 
	 */
	public int getItemCountSince(Date begin);
	
	public String getName();

	public long getTotalTimeInSec();
	
	public float getTotalTimeInHours();

	public long getTotalTimeInSecSince(Date begin);
	
	public float getTotalTimeInHoursSince(Date begin);
	
	public boolean hasItemInProgress();
	
	public boolean finishLastItem(Date when);
	
	public boolean finishLastItem();
	
	public void setEnabled(boolean state);
	
	public boolean isEnabled();
	
	
	/**
	 * Set energy consumption of logged peripheria as Wh. 
	 * @param wattValuePerHour
	 */
	public void setConsumpltion(float wattValuePerHour);
	
	public float getConsumpltionPerHour();
	
	public float getTotalConsumption();
	
	public float getTotalConsumption(Date since);
	
	public EnergyConsumptionInfo getEnergyConsumptionInfo();
	
}
