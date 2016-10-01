package hu.barbar.tasker.util.useagelog;

import java.util.Date;

public interface UseageLogIF {

	public Date getStartDate();
	
	public void add(UseageLogItem item);
	
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
