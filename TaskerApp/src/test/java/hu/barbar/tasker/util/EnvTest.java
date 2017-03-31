package hu.barbar.tasker.util;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.framework.TestSuite;

public class EnvTest extends TestSuite {


	@Test
	public void guaranteePathSeparatorAtEndOf_Test(){

		if(Env.runningOnTargetDevice()){
			String inputPath1 = "/home/pi/taskerData";
			assertEquals("/home/pi/taskerData/", Env.guaranteePathSeparatorAtEndOf(inputPath1));
		}else{
			String inputPath1 = "c:\\Some\\folder\\taskerData";
			assertEquals("c:\\Some\\folder\\taskerData\\", Env.guaranteePathSeparatorAtEndOf(inputPath1));
			
			String inputPath2 = "c:\\Some\\other folder\\taskerData\\";
			assertEquals("c:\\Some\\other folder\\taskerData\\", Env.guaranteePathSeparatorAtEndOf(inputPath2));
		}

	}

}
