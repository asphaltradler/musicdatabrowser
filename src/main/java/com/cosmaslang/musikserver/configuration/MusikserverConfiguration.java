package com.cosmaslang.musikserver.configuration;

import com.cosmaslang.musikserver.services.MusikserverStartupConfigurableService;
import com.cosmaslang.musikserver.services.MusikserverStartupService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.logging.Logger;

@Configuration
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "musikserver")
@EnableJpaRepositories("com.cosmaslang.musikserver.db.repositories")
public class MusikserverConfiguration {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private String rootdir;

    public void setRootdir(String rootdir) {
        logger.info("setting root Directory=" + rootdir);
        this.rootdir = rootdir;
    }

    @Bean
    @Primary
    MusikserverStartupService getService() {
        MusikserverStartupService service = new MusikserverStartupConfigurableService();
        service.setRootDir(rootdir);
        return service;
    }
}
