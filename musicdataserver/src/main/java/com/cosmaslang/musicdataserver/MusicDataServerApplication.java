package com.cosmaslang.musicdataserver;

import com.cosmaslang.musicdataserver.services.MusicDataServerStartupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class MusicDataServerApplication {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(MusicDataServerApplication.class);
        MusicDataServerStartupService service = context.getBean(MusicDataServerStartupService.class);
        service.configure();
        if (args.length <= 0 || !args[args.length - 1].equals("-noreload")) {
            service.init();
        }
        service.deleteOrphans();
        service.start();
    }
}
