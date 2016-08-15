package hu.barbar.tasker.todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hu.barbar.tasker.todo.items.ToDoItemBase;
import hu.barbar.util.logger.Log;

public class Worker extends Thread {

	
	/*
	 *  Class related attributes
	 */
	
	private static final String DEFAULT_WORKER_TITLE = "Unnamed worker"; 
	
	private static final long DEFAULT_DELAY_IN_SECONDS = 60;
	
	private static final int MILLISECONDS_IN_A_SECONDS = 1000;
	
	private static final String DATETIME_PATTERN_FOR_LASTRUN_DATE = "yyyy.MM.dd HH:mm:ss";
	
	protected static SimpleDateFormat sdf = null;
	
	
	/**
	 *  Common list of all worker instances
	 */
	private static ArrayList<Worker> workerList = new ArrayList<Worker>();

	
	/*
	 *  Instance related attributes
	 */
	
	private boolean needToRun = false;
	
	private ArrayList<ToDoItemBase> items = null;
	
	private String title = null; 
		
	private long delayInSec = Worker.DEFAULT_DELAY_IN_SECONDS;
	
	private Date lastRun = null;
	
	
	/*
	 *  Methods
	 */
	
	public Worker(long delayInSeconds) {
		init(Worker.DEFAULT_WORKER_TITLE, delayInSeconds);
	}
	
	public Worker(String title, long delayInSeconds) {
		init(title, delayInSeconds);
	}
	
	
	private void init(String title, long delayInSeconds){
		items = new ArrayList<ToDoItemBase>();
		this.delayInSec = delayInSeconds;
		this.title = title;
		Worker.workerList.add(this);
		if(sdf == null){
			sdf = new SimpleDateFormat(Worker.DATETIME_PATTERN_FOR_LASTRUN_DATE);
		}
	}
	
	
	public void addToDoItem(ToDoItemBase itemToAdd){
		this.items.add(itemToAdd);
	}
	
	public void setNeedToRun(boolean state){
		this.needToRun = state;
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getLastRun(){
		return this.lastRun;
	}
	
	public void setLastRun(Date date){
		this.lastRun = date;
	}
	
	public boolean isRanEverBefore(){
		return (this.lastRun != null);
	}
	
	
	/**
	 * @return a common ArrayList with all existing worker instances
	 */
	public static ArrayList<Worker> getWorkerList(){
		return Worker.workerList;
	}


	public String toString(){
		return this.toString(" ");
	}
	
	/**
	 * @param attribute_separator used after each attributes
	 * @return
	 */
	public String toString(String attribute_separator){
		String itemsLine = "";
		for(int i=0; i<items.size(); i++){
			itemsLine += "\t" + items.get(i).toString();
			if(i < items.size()-1){
				itemsLine += attribute_separator;
			}
		}
		return "Title: " + this.getTitle() + attribute_separator +
			   "Frequency: " + Long.toString(this.delayInSec) + "s" + attribute_separator +
			   "Date of last run: " + (this.isRanEverBefore()?(sdf.format(this.lastRun) ):("worker never ran before")) + attribute_separator +
			   "# of ToDoItems: " + Integer.toString(items.size()) + attribute_separator +
			   "Items: {\n" + itemsLine + "\n}"
		;
	}
	
	
	public int setStateForItems(ArrayList<Integer> idsOfItemsToSetNewState, boolean state){
		int modifiedItemCount = 0;
		
		for(int i=0; i<this.items.size(); i++){
			for(int j = 0; j < idsOfItemsToSetNewState.size(); j++){
				if( Integer.valueOf(items.get(i).getId()) == idsOfItemsToSetNewState.get(j)){
					items.get(i).setEnabled(state);
					modifiedItemCount++;
				}
			}
		}
		
		return modifiedItemCount;
	}
	

	@Override
	public void run() {
		
		this.needToRun = true;
		
		while(needToRun){
			
			/*
			 *  Execute child items.. 
			 */
			for(int i=0; i<items.size(); i++){
				if( items.get(i).isEnabled() && items.get(i).needToRun() ){
					items.get(i).execute();
				}
			}
			//  Store current date in lastRun attribute
			this.lastRun = new Date();
			
			
			/*
			 *  Wait for next iteration
			 */
			try {
				Thread.sleep(this.delayInSec * MILLISECONDS_IN_A_SECONDS);
			} catch (InterruptedException e) {
				Log.e("PROBLEM @ ToDoExecutor.run -> sleep..");
			}
			
		}
		
		//super.run();
	}
	
	
}
