package hu.barbar.tasker.util.useagelog;

import java.util.ArrayList;
import java.util.Date;

public class UseageLog implements UseageLogIF {

	private static final long MILLISEC_IN_SEC = 1000;
	private ArrayList<UseageLogItem> items = null;
	private Date creationDate = null;
	private String name = null;
	
	public UseageLog(){
		init();
	}
	
	public UseageLog(String name){
		init();
		this.name = name;
	}
	
	
	private void init(){
		items = new ArrayList<UseageLogItem>();
		creationDate = new Date();
	}
	
	
	public Date getStartDate() {
		return creationDate;
	}

	public void add(UseageLogItem item) {
		if(this.hasItemInProgress()){
			this.finishLastItem();
		}
		items.add(item);
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
	
	private UseageLogItem getLastItem() {
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

	
}
