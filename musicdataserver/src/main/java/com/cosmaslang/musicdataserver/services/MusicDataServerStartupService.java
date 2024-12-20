package com.cosmaslang.musicdataserver.services;

import java.io.IOException;

public interface MusicDataServerStartupService {
    void configure() throws IOException;
    void init() throws IOException;
    void start();
    String getInfo();
    void deleteOrphans();
}
