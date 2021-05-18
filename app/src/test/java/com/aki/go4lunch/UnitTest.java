package com.aki.go4lunch;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertTrue;

/**
 * Error margins can be modified to suit your computer performances !!
 * For me, a margin of around 20 millis is enough.
 */
public class UnitTest {
    Calendar dueDate;

    // Setting up dueDate at 12:00 to be sure my tests aren't biased
    @Before
    public void setup() {
        dueDate = Calendar.getInstance();

        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
    }

    // Test to see if the due date is later than the "actual" time (testDate)
    // It should send the time before 12pm
    @Test
    public void dueDateLater() {
        Calendar testDate = Calendar.getInstance();

        testDate.set(Calendar.HOUR_OF_DAY, 11);
        testDate.set(Calendar.MINUTE, 0);
        testDate.set(Calendar.SECOND, 0);

        if (dueDate.before(testDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - testDate.getTimeInMillis();

        // Value should be around 3600000 (with a little bit of error margin)
        //3600000 because 1h is 3.600.000 millis (12 /dueDate/ - 11 /testDate/ = 1)
        assertTrue(timeDiff >= 3599990 && timeDiff <= 3600010);
    }

    // Test to see if the due date is before than the "actual" time (testDate).
    // It should send the time before the next day at 12pm
    @Test
    public void dueDateBefore() {
        Calendar testDate = Calendar.getInstance();

        testDate.set(Calendar.HOUR_OF_DAY, 13);
        testDate.set(Calendar.MINUTE, 0);
        testDate.set(Calendar.SECOND, 0);

        if (dueDate.before(testDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - testDate.getTimeInMillis();


        // Value should be around 82800000 (with a little bit of error margin)
        // 82800000 because 23h is 82.800.000 millis (12+24 /dueDate/ - 13 /testDate/ = 36-13 = 23)
        assertTrue(timeDiff >= 82799990 && timeDiff <= 82800010);
    }
}