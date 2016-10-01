package hu.barbar.tasker.util.usagelog;

import java.util.Date;

public interface UsageLogIF {

	public Date getStartDate();
	
	public void add(UsageLogItem item);
	
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

	public long getTotalTimeInSecSince(Date begin);
	
	public boolean hasItemInProgress();
	
	public boolean finishLastItem(Date when);
	
	public boolean finishLastItem();
	
}
