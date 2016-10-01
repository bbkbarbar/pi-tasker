package hu.barbar.tasker.util.usagelog;

import java.util.ArrayList;
import java.util.Date;

public class UsageLog implements UsageLogIF {

	private static final long MILLISEC_IN_SEC = 1000;
	
	private static final float ENERGY_CONSUMPTION_UNDEFINED = -2;

	private static final long SECS_IN_AN_HOUR = 3600;

	
	private ArrayList<UsageLogItem> items = null;
	
	private Date creationDate = null;
	
	private String name = null;
	
	private boolean enabled = false;
	
	private float energyConsumption = ENERGY_CONSUMPTION_UNDEFINED;
	
	
	public UsageLog(){
		init();
	}
	
	public UsageLog(String name){
		init();
		this.name = name;
		this.enabled = true;
	}
	
	
	private void init(){
		items = new ArrayList<UsageLogItem>();
		creationDate = new Date();
	}
	
	
	public Date getStartDate() {
		return creationDate;
	}

	public void add(UsageLogItem item) {
		if(this.hasItemInProgress()){
			this.finishLastItem();
		}
		items.add(item);
	}
	
	public void addNewLogItem() {
		UsageLogItem item = new UsageLogItem(new Date());
		this.add(item);
	}

	public int getItemCount() {
		return items.size();
	}
	
	public String getName(){
		return this.name;
	}

	public long getTotalTimeInSec() {
		
		long totalTimeInSec = 0;
		for(int i=0; i<items.size(); i++){
			totalTimeInSec += (items.get(i).getElaspedTimeInMs() / MILLISEC_IN_SEC);
		}
		
		return totalTimeInSec;
	}


	public long getTotalTimeInSecSince(Date begin) {
		long totalTimeInSec = 0;
		for(int i=0; i<items.size(); i++){
			totalTimeInSec += (items.get(i).getElaspedTimeInMs(begin) / MILLISEC_IN_SEC);
		}
		return totalTimeInSec;
	}

	
	public int getItemCountSince(Date begin) {
		int count = 0;
		
		for(int i=0; i<items.size(); i++){
			if ( (items.get(i).isFinished() && items.get(i).getEndDate().after(begin))
			   || (items.get(i).isInProgress()) && items.get(i).getStartDate().after(begin) ) {
				count += 1;
			}
		}
		
		return count;
	}

	public boolean finishLastItem(Date when) {
		
		if(this.hasItemInProgress() == false){
			return false;
		}
		
		Date finishDate;
		if(when == null){
			finishDate = new Date();
		}else{
			finishDate = when;
		}
		
		this.getLastItem().setEndDate(finishDate);
		
		return false;
	}
	
	private UsageLogItem getLastItem() {
		if(this.items == null || this.items.size() == 0){
			return null;
		}else{
			return this.items.get(this.items.size()-1);
		}
	}

	public boolean finishLastItem() {
		return this.finishLastItem(new Date());
	}

	/**
	 *  Note: Check only the last item!!!
	 */
	public boolean hasItemInProgress() {
		if(items == null || items.size() == 0){
			return false;
		}
		return items.get(items.size()-1).isInProgress();
	}

	
	public void setConsumpltion(float wattValuePerHour) {
		this.energyConsumption = wattValuePerHour;
	}

	public void setEnabled(boolean state) {
		this.enabled = state;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public float getTotalTimeInHours() {
		return ((float)getTotalTimeInSec() / SECS_IN_AN_HOUR);
	}

	public float getTotalTimeInHoursSince(Date begin) {
		return ((float)getTotalTimeInSecSince(begin) / SECS_IN_AN_HOUR);
	}

	public float getConsumpltionPerHour() {
		return this.energyConsumption;
	}

	public float getTotalConsumption() {
		return this.getTotalTimeInHours() * this.getConsumpltionPerHour();
	}

	public float getTotalConsumption(Date since) {
		return this.getTotalTimeInHoursSince(since) * this.getConsumpltionPerHour();
	}

	public EnergyConsumptionInfo getEnergyConsumptionInfo() {
		return new EnergyConsumptionInfo(creationDate, this.getTotalTimeInHours(), energyConsumption);
	}
	
	public EnergyConsumptionInfo getEnergyConsumptionInfo(Date since) {
		return new EnergyConsumptionInfo(creationDate, this.getTotalTimeInHoursSince(since), energyConsumption);
	}
	
}
