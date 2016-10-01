package hu.barbar.PiTasker.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import hu.barbar.tasker.util.UseageLog;
import hu.barbar.tasker.util.UseageLogItem;
import junit.framework.TestSuite;

public class UseageLogTest extends TestSuite {
	
	private UseageLog log;
	private Date date1, date2;
	
	@Test
	public void heaterLogCreateTest(){
		Date logCreationDate = new Date();
		log = new UseageLog();
		assertNotNull(log.getStartDate());
		long diff = (log.getStartDate().getTime() - logCreationDate.getTime());
		assertTrue( diff  < 100 );
	}
	
	@Test
	public void heaterLogGetLogCount(){
		log = new UseageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UseageLogItem item = new UseageLogItem(date1, date2);
		log.add(item);
		assertEquals(1, log.getItemCount());
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UseageLogItem(date1, date2);
		log.add(item);
		assertEquals(2, log.getItemCount());
	}
	
	@Test
	public void heaterLogGetTotalTime(){
		log = new UseageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UseageLogItem item = new UseageLogItem(date1, date2);
		log.add(item);
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UseageLogItem(date1, date2);
		log.add(item);
		
		assertEquals(5400, log.getTotalTimeInSec());
	}

	@Test
	public void heaterLogGetTotalTimeSince1Test(){
		log = new UseageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UseageLogItem item = new UseageLogItem(date1, date2);
		log.add(item);
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UseageLogItem(date1, date2);
		log.add(item);
		
		Date begin = new Date(2016, 9, 30, 9, 30, 0);
		assertEquals(1800, log.getTotalTimeInSecSince(begin));
	}
	
	@Test
	public void heaterLogGetTotalTimeSince2Test(){
		log = new UseageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UseageLogItem item = new UseageLogItem(date1, date2);
		log.add(item);
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UseageLogItem(date1, date2);
		log.add(item);
		
		Date begin = new Date(2016, 9, 30, 8, 30, 0);
		assertEquals(3600, log.getTotalTimeInSecSince(begin));
	}
}