package org.pangu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

/**
 * Hello world!
 *
 */
@SpringBootApplication()
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main( String[] args ){
        logger.info("Hello task eye rest!");
        SpringApplication.run(Application.class, args);
    }

    @Bean(name="forwardedHeaderTransformer")
    public ForwardedHeaderTransformer getForwardedHeaderTransformer(){
        ForwardedHeaderTransformer fht = new ForwardedHeaderTransformer();
        fht.setRemoveOnly(true);
        return fht;
    }
}
