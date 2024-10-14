package com.cosmaslang.musikdataserver.services;

public interface MusikDataServerStartupService {

    void setRootDir(String rootDir);

    void init();

    void start();

}
