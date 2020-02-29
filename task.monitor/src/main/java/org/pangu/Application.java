package org.pangu;

import org.pangu.command.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class Application
{
    private static Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main( String[] args )
    {
        logger.info("Hello task eye monitor!");
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");
            WorkerService workerService = ctx.getBean(WorkerService.class);
            workerService.run();
        };
    }

}
