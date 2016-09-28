package hu.barbar.tasker.util;

import java.util.Date;

import hu.barbar.tasker.util.exceptions.NotFinishedYetException;

public class HeaterLogItem {

	private Date startDate = null;
	private Date endDate = null;
	
	public HeaterLogItem(Date startDate){
		//TODO implement me
	}
	
	public HeaterLogItem(Date startDate, Date endDate){
		//TODO implement me
	}
	
	public boolean isInProgress(){
		return (endDate == null);
	}
	
	public boolean isStarted(){
		return (this.startDate != null);
	}
	
	public boolean isFinished(){
		return !this.isInProgress();
	}
	
	/**
	 * Get elasped time between startDate and endDate in ms.
	 * @return
	 * @throws NotFinishedYetException
	 */
	public long getElaspedTimeInMs() throws NotFinishedYetException {
		return 0;
	}
	
	public Date getStartDate(){
		return this.startDate;
	}
	
	public Date getEndDate(){
		return this.endDate;
	}
	
}
