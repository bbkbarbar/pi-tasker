package hu.barbar.PiTasker.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import hu.barbar.tasker.util.UseageLogItem;
import hu.barbar.tasker.util.exceptions.ItemFinishedException;
import hu.barbar.tasker.util.exceptions.NotFinishedYetException;
import junit.framework.TestSuite;

public class UseageLogItemTest extends TestSuite {

	private static Date date1, date2;
	
	@SuppressWarnings("deprecation")
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
		
		boolean exceptionCought = false;
		try {
			item.getElaspedTimeInMs();
		} catch (NotFinishedYetException e1) {
			exceptionCought = true;
		}
		assertTrue(exceptionCought);
		
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
		long diff = -1;
		try {
			diff = item.getElaspedTimeInMs();
		} catch (NotFinishedYetException e) {
			fail("Exception cought.");
		}
		assertEquals(3600*1000, diff);
	}
	
}
