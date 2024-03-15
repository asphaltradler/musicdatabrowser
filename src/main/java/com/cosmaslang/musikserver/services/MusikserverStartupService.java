package com.cosmaslang.musikserver.services;

public interface MusikserverStartupService {
    String getRootDir();
    void setRootDir(String rootDir);
    void start();
}
