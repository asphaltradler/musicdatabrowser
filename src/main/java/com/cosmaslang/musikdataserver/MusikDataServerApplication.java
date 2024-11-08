package com.cosmaslang.musikdataserver;

import com.cosmaslang.musikdataserver.services.MusikDataServerStartupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class MusikDataServerApplication {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MusikDataServerApplication.class);
        MusikDataServerStartupService service = context.getBean(MusikDataServerStartupService.class);
        if (args.length <= 0 || !args[args.length - 1].equals("-noreload")) {
            service.init();
        }
        service.start();
    }

}
