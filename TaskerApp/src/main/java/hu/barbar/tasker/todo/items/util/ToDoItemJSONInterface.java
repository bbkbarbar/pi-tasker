package hu.barbar.tasker.todo.items.util;

import org.json.simple.JSONObject;

/**
 * 	Interface to store attributes of ToDoItems as a JSON file and <br>
 *  load ToDoItem attributes from JSON file.
 * 
 * @author Barbar
 */
public interface ToDoItemJSONInterface {

	/*
	 * Create instance from Json object (what can be loaded from file).
	 * @param json
	 */
	/*
	public void loadFromJSON(JSONObject json);
	/**/
	
	/**
	 * Used in {@link getAsJSON()} method identify that what class the JSON content made from. 
	 * @return 
	 */
	public String getClassName();
	
	/**
	 * Store instance as Json object (to be possible to save as file).
	 * @return a JSONObject instance what contains all attributes of ToDoItem instance.
	 */
	public JSONObject getAsJSON();
	
}
