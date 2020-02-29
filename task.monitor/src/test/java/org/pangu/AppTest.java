package org.pangu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pangu.common.util.Helper;

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
        Date d = Helper.getTimeBefor(new Date(), 10*60L);
        Date lastCronDate = Helper.getNextDate("0 /10 * * * ? *", d);
        System.out.println(new Date());
        System.out.println(d);
        System.out.println(lastCronDate);
    }
}
