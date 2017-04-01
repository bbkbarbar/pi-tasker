package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestSuite;

public class EnvTest extends TestSuite {

	private boolean debugMode = false;
	
	@Before
	public void before(){
		//System.out.println(FileHandler.readJSON(configJsonFilePath));
	}

	@Test
	public void fixPathSeparators_from_win_on_win_Test(){
		if(debugMode)
			System.out.println("Run fixPathSeparator test on Windows.");
		
		Env.setOSMock(Env.OsName.WINDOWS);
		String originalPath = "alma\\korte\\";
		String modified = Env.fixPathSeparators(originalPath);
		assertEquals("alma\\korte\\", modified);
	}
	
	@Test
	public void fixPathSeparators_from_linux_on_win_Test(){
		if(debugMode)
			System.out.println("Run fixPathSeparator test on Windows.");
		
		Env.setOSMock(Env.OsName.WINDOWS);
		String originalPath = "alma/korte/";
		String modified = Env.fixPathSeparators(originalPath);
		assertEquals("alma\\korte\\", modified);
	}
	
	@Test
	public void fixPathSeparators_from_win_on_linux_Test(){
		if(debugMode)
			System.out.println("Run fixPathSeparator test on linux.");
		
		Env.setOSMock(Env.OsName.LINUX);
		String originalPath = "alma\\korte\\";
		String modified = Env.fixPathSeparators(originalPath);
		assertEquals("alma/korte/", modified);
	}
	
	@Test
	public void fixPathSeparators_from_linux_on_linux_Test(){
		if(debugMode)
			System.out.println("Run fixPathSeparator test on linux.");
		
		Env.setOSMock(Env.OsName.LINUX);
		String originalPath = "alma/korte/";
		String modified = Env.fixPathSeparators(originalPath);
		assertEquals("alma/korte/", modified);
	}
	
	@Test
	public void fixPathSeparators_NULL_Test(){
		String result = Env.fixPathSeparators(null);
		assertEquals(null, result);
	}
	
	@After
	public void after(){
		Env.setOSMock(Env.OsName.NO_MOCK_USED);
		if(debugMode)
			System.out.println("Env mocking disabled.");
	}
	
}
