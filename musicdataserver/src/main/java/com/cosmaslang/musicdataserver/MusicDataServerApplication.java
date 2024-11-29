package com.cosmaslang.musicdataserver;

import com.cosmaslang.musicdataserver.services.MusicDataServerStartupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class MusicDataServerApplication {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MusicDataServerApplication.class);
        MusicDataServerStartupService service = context.getBean(MusicDataServerStartupService.class);
        if (args.length <= 0 || !args[args.length - 1].equals("-noreload")) {
            service.init();
        }
        service.start();
    }

}
