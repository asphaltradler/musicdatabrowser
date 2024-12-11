package com.cosmaslang.musicdataserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.logging.Logger;

@Configuration
@ConfigurationProperties(prefix = "musicdataserver")
@EnableJpaAuditing
public class MusicDataServerConfiguration {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Path rootPath;
    private Path startPath;
    private int pageSize;
    private int documentRefreshTimeInMinutes;

    /**
     * Automatisch gesetzt aus application.properties
     */
    public void setRootPath(String rootPath) {
        logger.config(MessageFormat.format("setting root directory={0} from config", rootPath));
        this.rootPath = Path.of(rootPath);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void setStartPath(String startPath) {
        logger.config(MessageFormat.format("setting start directory={0} from config", startPath));
        this.startPath = Path.of(startPath);
    }

    public Path getStartPath() {
        return startPath;
    }

    public void setPageSize(int pageSize) {
        logger.config(MessageFormat.format("setting pageSizeDefault={0} from config", pageSize));
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setDocumentRefreshTimeInMinutes(int documentRefreshTimeInMinutes) {
        logger.config(MessageFormat.format("setting documentRefreshTimeInHours={0} from config", documentRefreshTimeInMinutes));
        this.documentRefreshTimeInMinutes = documentRefreshTimeInMinutes;
    }

    public int getDocumentRefreshTimeInMinutes() {
        return documentRefreshTimeInMinutes;
    }

    public String getRelativePath(Path path) {
        return rootPath.relativize(path).normalize().toString().replace('\\', '/');
    }

    public File getFileFromRelativePath(Path relativePath) {
        return rootPath.resolve(relativePath).toFile();
    }
}
