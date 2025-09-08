package de.pbma.moa.database;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("de.pbma.moa.database.test", appContext.getPackageName());
    }
    @Test
    public void sortTournament() {
        ArrayList<Tournament> array = new ArrayList<Tournament>();
        Tournament t1 = new Tournament();
        Tournament t2 = new Tournament();
        Tournament t3 = new Tournament();
        Tournament t4 = new Tournament();
        Tournament t5 = new Tournament();
        Tournament t6 = new Tournament();
        Tournament t7 = new Tournament();
        t1.timestamp ="10/1/2025 11:50:42";
        t2.timestamp ="11/1/2025 11:50:43";
        t3.timestamp ="11/1/2025 11:50:44";
        t4.timestamp ="11/1/2025 11:50:45";
        t5.timestamp ="11/1/2025 11:50:46";
        t6.timestamp ="11/1/2025 11:50:47";
        t7.timestamp ="11/1/2025 13:50:42";

        array.add(t1);
        array.add(t2);
        array.add(t3);
        array.add(t4);
        array.add(t5);
        array.add(t6);
        array.add(t7);

        Collections.sort(array);

        for(Tournament t: array){
            System.out.printf("\n%s", t.timestamp);
        }
    }
}