package hu.barbar.tasker.todo.items;

import java.util.Comparator;

import hu.barbar.tasker.todo.items.tempcontrol.TempController.RuleItem;

/**
 * Comparator class to sort RuleItem instances
 * 
 * @author Barbar
 */
public class RuleItemComparator implements Comparator<RuleItem> {

	@Override
	public int compare(RuleItem o1, RuleItem o2) {
		
		if(o1.getLimitValue() < o2.getLimitValue()){
			return -1;
		}
		if(o1.getLimitValue() > o2.getLimitValue()){
			return 1;
		}
		
		if(o1.getThreshold() < o2.getThreshold()){
			return -1;
		}
		if(o1.getThreshold() > o2.getThreshold()){
			return 1;
		}
		
		if(o1.getOutputValue() < o2.getOutputValue()){
			return -1;
		}
		if(o1.getOutputValue() > o2.getOutputValue()){
			return 1;
		}
		
		return 0;
	}
	
}