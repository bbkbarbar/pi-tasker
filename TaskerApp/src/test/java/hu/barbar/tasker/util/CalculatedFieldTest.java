package hu.barbar.tasker.util;

import hu.barbar.tasker.todo.items.util.CalculatedField;
import junit.framework.TestSuite;
import org.junit.Before;
import org.junit.Test;

public class CalculatedFieldTest extends TestSuite {

        @Before
        public void before(){
        }

        @Test
        public void calculateVaporDensityForTemperatureTest(){
            double tempC = 30;
            Double d = CalculatedField.calculateMaxVaporDensityForTemperature(tempC);
            System.out.println(tempC + "°C - " + d);
        }
}
