package hu.barbar.PiTasker.util.useagelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import hu.barbar.tasker.util.useagelog.UseageLogItem;
import hu.barbar.tasker.util.useagelog.exceptions.ItemFinishedException;
import junit.framework.TestSuite;

@SuppressWarnings("deprecation")
public class UseageLogItemTest extends TestSuite {

	private static Date date1, date2;
	
	@Before
	public void setUp(){
	}
	
	
	@Test
	public void troughPutTest(){
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = null;
		UseageLogItem item = new UseageLogItem(date1);
		assertFalse(item.isFinished());
		assertTrue(item.isInProgress());
		
		date2 = new Date(2016, 9, 30, 8, 30, 0);
		try {
			item.addEndDate(date2);
		} catch (ItemFinishedException e) {
			fail("Can not add end date.");
		}
		assertTrue(item.isFinished());
		assertFalse(item.isInProgress());
		
	}
	
	
	@Test
	public void elaspedTimeTest(){
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		
		UseageLogItem item = new UseageLogItem(date1, date2);
		long diff = item.getElaspedTimeInMs();
		assertEquals(3600*1000, diff);
	}
	
}
