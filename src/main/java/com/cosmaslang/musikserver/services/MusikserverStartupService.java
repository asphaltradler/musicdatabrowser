package com.cosmaslang.musikserver.services;

public interface MusikserverStartupService {

    void setRootDir(String rootDir);

    void init();
    void start();

}
