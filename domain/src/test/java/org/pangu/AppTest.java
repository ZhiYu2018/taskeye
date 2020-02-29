package org.pangu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pangu.common.util.Helper;


import java.text.ParseException;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testCron() throws ParseException {
        Date now = Helper.strToDate("2020-02-29 17:26:00");
        System.out.println("hh:" + now.toString());
        for(int i = 0; i < 10; i++) {
            Date nd = Helper.getNextDate("/40 * * * * ? *", now);
            System.out.println(nd.toString());
            //now = nd;
        }
    }
}
