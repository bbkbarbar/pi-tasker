package hu.barbar.tasker.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import hu.barbar.tasker.util.exceptions.ItemFinishedException;
import hu.barbar.tasker.util.exceptions.NotFinishedYetException;

public class HeaterLogItem {

	private Date startDate = null;
	private Date endDate = null;
	
	public HeaterLogItem(Date startDate){
		this.startDate = startDate;
		this.endDate = null;
	}
	
	public HeaterLogItem(Date startDate, Date endDate){
		this.startDate = startDate;
		this.endDate = endDate;
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
		if(isFinished()){
			return getDateDiff(startDate, endDate);
		}else{
			throw new NotFinishedYetException();
		}
	}
	
	public long getElaspedTimeUntilNowInMs() {
		return getDateDiff(startDate, new Date());
	}
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @return the diff value, in the provided unit
	 */
	private static long getDateDiff(Date date1, Date date2) {
		return date2.getTime() - date1.getTime();
	}
	
	public Date getStartDate(){
		return this.startDate;
	}
	
	public Date getEndDate(){
		return this.endDate;
	}

	public void addEndDate(Date endDate) throws ItemFinishedException {
		if(this.isInProgress()){
			this.endDate = endDate;
		}else{
			throw new ItemFinishedException();
		}
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getElaspedTimeInMsOnlySince(Date begin) {
		return getDateDiff(begin, this.endDate);
	}

	public long getElaspedTimeUntilNowInMsOnlySince(Date begin) {
		return getDateDiff(begin, new Date());
	}

}
