package com.cosmaslang.musikserver;

import com.cosmaslang.musikserver.services.MusikserverStartupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class MusikserverApplication {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MusikserverApplication.class);
        MusikserverStartupService service = context.getBean(MusikserverStartupService.class);
        if (args.length <= 0 || !args[0].equals("-noreload")) {
            service.init();
        }
        service.start();
    }

}
