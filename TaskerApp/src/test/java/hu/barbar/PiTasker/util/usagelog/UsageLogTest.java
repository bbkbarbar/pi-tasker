package hu.barbar.PiTasker.util.usagelog;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import hu.barbar.tasker.util.usagelog.UsageLog;
import hu.barbar.tasker.util.usagelog.UsageLogItem;
import junit.framework.TestSuite;

@SuppressWarnings("deprecation")
public class UsageLogTest extends TestSuite {
	
	private UsageLog log;
	private Date date1, date2;
	
	@Test
	public void heaterLogCreateTest(){
		Date logCreationDate = new Date();
		log = new UsageLog();
		assertNotNull(log.getStartDate());
		long diff = (log.getStartDate().getTime() - logCreationDate.getTime());
		assertTrue( diff  < 100 );
	}
	
	@Test
	public void heaterLogGetLogCount(){
		log = new UsageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		assertEquals(1, log.getItemCount());
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UsageLogItem(date1, date2);
		log.add(item);
		assertEquals(2, log.getItemCount());
	}
	
	@Test
	public void heaterLogGetTotalTimeTest(){
		log = new UsageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UsageLogItem(date1, date2);
		log.add(item);
		
		assertEquals(5400, log.getTotalTimeInSec());
	}

	@Test
	public void heaterLogGetTotalTimeSince1Test(){
		log = new UsageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UsageLogItem(date1, date2);
		log.add(item);
		
		Date begin = new Date(2016, 9, 30, 9, 30, 0);
		assertEquals(1800, log.getTotalTimeInSecSince(begin));
	}
	
	
	@Test
	public void heaterLogGetTotalTimeSince2Test(){
		log = new UsageLog();
		
		Date begin = new Date(2016, 9, 30, 8, 30, 0);
		assertEquals(0, log.getTotalTimeInSecSince(begin));
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		assertEquals(1800, log.getTotalTimeInSecSince(begin));
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UsageLogItem(date1, date2);
		log.add(item);
		
		assertEquals(3600, log.getTotalTimeInSecSince(begin));
	}
	
	@Test
	public void heaterLogGetTotalTimeSince3Test(){
		log = new UsageLog();
		
		Date begin = new Date(2016, 9, 30, 11, 30, 0);
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		assertEquals(0, log.getTotalTimeInSecSince(begin));
	}
	
	@Test
	public void heaterLogGetTotalTimeSince4Test(){
		log = new UsageLog();
		
		Date futureDate = new Date(2116, 9, 30, 8, 0, 0);
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		assertEquals(0, log.getTotalTimeInSecSince(futureDate));
	}
	
	@Test
	public void getItemCountSinceTest(){
		log = new UsageLog();
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);

		Date begin = new Date(2016, 9, 30, 9, 30, 0);
		assertEquals(0, log.getItemCountSince(begin));
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		date2 = new Date(2016, 9, 30, 10, 30, 0);
		item = new UsageLogItem(date1, date2);
		log.add(item);
		
		assertEquals(1, log.getItemCountSince(begin));
		
		
		date1 = new Date();
		item = new UsageLogItem(date1);
		log.add(item);
		try {
			Thread.sleep(1001);
		} catch (InterruptedException e) {
		}
		begin = new Date();
		assertEquals(2, log.getItemCountSince(begin));
		
	}
	
	@Test
	public void hasItemInProgressTest(){

		log = new UsageLog();
		assertFalse(log.hasItemInProgress());
		
		date1 = new Date(2016, 9, 30, 8, 0, 0);
		date2 = new Date(2016, 9, 30, 9, 0, 0);
		UsageLogItem item = new UsageLogItem(date1, date2);
		log.add(item);
		assertFalse(log.hasItemInProgress());
		
		
		date1 = new Date(2016, 9, 30, 10, 0, 0);
		item = new UsageLogItem(date1);
		log.add(item);
		assertTrue(log.hasItemInProgress());
		
		log.finishLastItem();
		assertFalse(log.hasItemInProgress());
		
	}
	
}