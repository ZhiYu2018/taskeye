package org.pangu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pangu.dto.PanguRequest;
import org.pangu.dto.PanguResponse;
import org.pangu.dto.TaskStatus;

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
    public void testFegin(){
        TaskEyeRestCaller caller = new TaskEyeRestCaller("http://127.0.0.1:8090");
        PanguRequest<TaskStatus> body = new PanguRequest<>();
        body.setAppId("TianJi");
        body.setFlowId(String.valueOf(System.currentTimeMillis()));
        TaskStatus status = new TaskStatus();
        status.setAppId("TianJi");
        status.setFlag(2);
        status.setMsg("OK");
        status.setOptId("2020-02-27 20:14:00");
        status.setTaskName("batch");
        body.setData(status);

        PanguResponse<String> response = caller.PostStatus(body);
        System.out.println(response.getStatus());
    }
}
