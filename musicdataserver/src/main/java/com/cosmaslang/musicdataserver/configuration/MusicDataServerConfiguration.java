package com.cosmaslang.musicdataserver.configuration;

import com.cosmaslang.musicdataserver.services.MusicDataServerStartupConfigurableService;
import com.cosmaslang.musicdataserver.services.MusicDataServerStartupService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;

@Configuration
//@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "musicdataserver")
//@EnableJpaRepositories("com.cosmaslang.musicdataserver.db.repositories")
@EnableJpaAuditing
public class MusicDataServerConfiguration {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private String rootdir;
    private String startdir;
    private int pageSize;

    /**
     * Automatisch gesetzt aus application.properties
     */
    public void setRootdir(String rootdir) {
        logger.info(MessageFormat.format("setting root directory={0} from config", rootdir));
        this.rootdir = rootdir;
    }

    public String getRootdir() {
        return rootdir;
    }

    public void setStartdir(String startdir) {
        logger.info(MessageFormat.format("setting start directory={0} from config", startdir));
        this.startdir = startdir;
    }

    public String getStartdir() {
        return startdir;
    }

    public void setPagesize(int pageSize) {
        logger.info(MessageFormat.format("setting pageSizeDefault={0} from config", pageSize));
        this.pageSize = pageSize;
    }

    public int getPagesize() {
        return pageSize;
    }

    @Bean
    @Primary
    MusicDataServerStartupService getService() throws IOException {
        return new MusicDataServerStartupConfigurableService();
    }
}
