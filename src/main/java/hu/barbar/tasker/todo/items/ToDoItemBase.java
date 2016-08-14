package hu.barbar.tasker.todo.items;

public abstract class ToDoItemBase {

	/**
	 * Static value to store the next if of any instance of any descendant class to avoid duplicated ids.
	 */
	private static int NEXT_ID = 0;
	
	/**
	 * @return the id of the next instance of ToDoItem class (or any descendant class)
	 */
	public static int getNextId(){
		int ret = ToDoItemBase.NEXT_ID;
		ToDoItemBase.NEXT_ID++;
		return ret;
	}
	
	public static final int ID_UNDEFINED = -1;
	
	
	protected int id = ID_UNDEFINED;
	
	protected boolean enabled = true;
	
	protected String title = null;
	
	
	public ToDoItemBase(){
		this.id = ToDoItemBase.getNextId();
		this.enabled = true;
		this.title = "";
	}
	
	
	public int getId(){
		return this.id;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	
	/**
	 *  This method will be called when Workers wants to create lists of their Items.
	 * @return the "name" of the descendant class.
	 */
	public abstract String getClassTitle();
	
	public String toString(){
		return "Id: " + this.getId() + " Enabled: [" + (this.isEnabled()?"X":" ") + "]" + " Type: " + this.getClassTitle() + (this.title == null || this.title.trim().equals("")?(""):" Title: " + this.getTitle());
	}
	
	
	/**
	 *  This method will be called when ToDoExecutor wants to run the TodoItem instance 
	 */
	public abstract void execute();
	
	
	/**
	 *  This method will be used to determine that need to run this ToDoItem now or not.
	 * @return true if need to run now or <br>
	 *         false if not.
	 */
	public abstract boolean needToRun();
	
}
