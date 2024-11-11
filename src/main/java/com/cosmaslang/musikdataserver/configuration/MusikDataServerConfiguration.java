package com.cosmaslang.musikdataserver.configuration;

import com.cosmaslang.musikdataserver.services.MusikDataServerStartupConfigurableService;
import com.cosmaslang.musikdataserver.services.MusikDataServerStartupService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.text.MessageFormat;
import java.util.logging.Logger;

@Configuration
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "musikdataserver")
@EnableJpaRepositories("com.cosmaslang.musikdataserver.db.repositories")
public class MusikDataServerConfiguration {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private String rootdir;

    /**
     * Automatisch gesetzt aus application.properties
     */
    public void setRootdir(String rootdir) {
        logger.info(MessageFormat.format("setting root directory={0} from config", rootdir));
        this.rootdir = rootdir;
    }

    @Bean
    @Primary
    MusikDataServerStartupService getService() {
        MusikDataServerStartupService service = new MusikDataServerStartupConfigurableService();
        service.setRootDir(rootdir);
        return service;
    }
}
