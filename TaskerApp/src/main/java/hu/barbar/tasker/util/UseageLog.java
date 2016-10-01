package hu.barbar.tasker.util;

import java.util.ArrayList;
import java.util.Date;

import hu.barbar.tasker.util.exceptions.NotFinishedYetException;

public class UseageLog {

	private static final long MILLISEC_IN_SEC = 1000;
	private ArrayList<UseageLogItem> items = null;
	private Date creationDate = null;
	
	
	public UseageLog(){
		items = new ArrayList<UseageLogItem>();
		creationDate = new Date();
	}
	
	
	public Date getStartDate() {
		return creationDate;
	}

	public void add(UseageLogItem item) {
		items.add(item);
	}

	public int getItemCount() {
		return items.size();
	}


	public long getTotalTimeInSec() {
		
		long totalTimeInSec = 0;
		for(int i=0; i<items.size(); i++){
			if(items.get(i).isFinished()){
				try {
					totalTimeInSec += (items.get(i).getElaspedTimeInMs() / MILLISEC_IN_SEC);
				} catch (NotFinishedYetException e) {}
			}else{
				totalTimeInSec += (items.get(i).getElaspedTimeUntilNowInMs() / MILLISEC_IN_SEC);
			}
		}
		
		return totalTimeInSec;
	}


	public long getTotalTimeInSecSince(Date begin) {
		long totalTimeInSec = 0;
		for(int i=0; i<items.size(); i++){
			totalTimeInSec += (items.get(i).getElaspedTimeInMsOnlySince(begin) / MILLISEC_IN_SEC);
		}
		return totalTimeInSec;
	}

	
}
