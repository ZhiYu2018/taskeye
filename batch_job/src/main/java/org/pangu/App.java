package org.pangu;

import org.pangu.common.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Date;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
    private static Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx){
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");
            QJob qJob = new QJob();
            AutoJob job = ctx.getBean(AutoJob.class);
            int t = 0;
            int f = 0;
            String cron = "30 * * * * ?";
            Date next = Helper.getNextDate(cron, new Date());
            while(t < 1000){
                Date now = new Date();
                if(now.before(next)){
                    try{
                        Thread.sleep(3000);
                    }catch (Throwable b){

                    }
                    continue;
                }

                try{
                    job.run("123456");
                    next = Helper.getNextDate(cron, new Date());
                }catch (Throwable b){
                    f ++;
                }

                t++;
            }

            qJob.stop();
        };
    }
}
