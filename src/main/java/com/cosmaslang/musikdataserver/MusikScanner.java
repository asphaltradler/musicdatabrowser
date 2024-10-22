package com.cosmaslang.musikdataserver;

import jakarta.persistence.PersistenceException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.SupportedFileFormat;
import org.jaudiotagger.audio.generic.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MusikScanner {

    public interface AudioFileProcessor {
        void processAudioFile(AudioFile audioFile);
    }

    private final AudioFileProcessor processor;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final static List<String> audioFileExtensions = Stream.of(SupportedFileFormat.values()).map(SupportedFileFormat::getFilesuffix).toList();

    private long count = 0;
    private long failed = 0;

    public MusikScanner(AudioFileProcessor processor) {
        super();
        this.processor = processor;
    }

    public void start(Path rootPath)  throws IOException {
        System.out.println("Scanning " + rootPath);
        scanDirectory(rootPath);
    }

    public void scanDirectory(final Path dir) throws IOException {
        try (Stream<Path> audioPaths = Files.list(dir)) {
            //Parallelisierung führt zu Problemen bei lazy initialization
            audioPaths./*parallel().*/forEach(path -> {
                try {
                    if (Files.isDirectory(path)) {
                        scanDirectory(path);
                    } else {
                        String ext = Utils.getExtension(path.toFile()).toLowerCase();
                        if (audioFileExtensions.contains(ext)) {
                            count++;
                            try {
                                AudioFile audioFile = AudioFileIO.read(path.toFile());
                                //logger.info("processed " + file.getName());
                                processor.processAudioFile(audioFile);
                            } catch (PersistenceException e) {
                                failed++;
                                throw e;
                            } catch (Throwable t) {
                                failed++;
                                logger.log(Level.WARNING, "Unable to read record:" + count + ":" + path, t);
                            }

                        }
                    }
                } catch (IOException e) {
                    failed++;
                    logger.log(Level.WARNING, "Unable to read: " + path);
                }
            });
        }
    }

    public long getCount() {
        return count;
    }

    public long getFailed() {
        return failed;
    }

}
