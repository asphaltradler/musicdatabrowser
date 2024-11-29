package com.cosmaslang.musicdataserver.configuration;

import com.cosmaslang.musicdataserver.services.MusikDataServerStartupConfigurableService;
import com.cosmaslang.musicdataserver.services.MusikDataServerStartupService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;

@Configuration
//@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "musicdataserver")
//@EnableJpaRepositories("com.cosmaslang.musicdataserver.db.repositories")
public class MusikDataServerConfiguration {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private String rootdir;
    private String startdir;

    /**
     * Automatisch gesetzt aus application.properties
     */
    public void setRootdir(String rootdir) {
        logger.info(MessageFormat.format("setting root directory={0} from config", rootdir));
        this.rootdir = rootdir;
    }

    public void setStartdir(String startdir) {
        logger.info(MessageFormat.format("setting start directory={0} from config", startdir));
        this.startdir = startdir;
    }

    @Bean
    @Primary
    MusikDataServerStartupService getService() throws IOException {
        MusikDataServerStartupService service = new MusikDataServerStartupConfigurableService();
        service.setMediaDirectories(rootdir, startdir);
        return service;
    }
}
