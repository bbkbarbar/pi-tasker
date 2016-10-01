package hu.barbar.tasker.util.useagelog;

import java.util.Date;

import hu.barbar.tasker.util.useagelog.exceptions.ItemFinishedException;

public class UseageLogItem {

	private Date startDate = null;
	private Date endDate = null;
	
	public UseageLogItem(Date startDate){
		this.startDate = startDate;
		this.endDate = null;
	}
	
	public UseageLogItem(Date startDate, Date endDate){
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
	 */
	public long getElaspedTimeInMs() {
		return this.getElaspedTimeInMs(null);
	}
	
	/**
	 * Get elasped time between startDate and endDate in ms.
	 * @param since
	 * @return
	 */
	public long getElaspedTimeInMs(Date since) {
		if(since == null || (since != null && since.before(this.startDate))){
			if(isFinished()){
				return getDateDiff(startDate, endDate);
			}else{
				return getDateDiff(startDate, new Date());
			}
		}else{
			
			if(isFinished()){
				if(since.before(this.endDate)){
					return getDateDiff(since, endDate);
				}else{
					return 0;
				}
			}else{
				Date now = new Date();
				if(since.before(now)){
					return getDateDiff(since, now);
				}else{
					return 0;
				}
			}
		}
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

}
