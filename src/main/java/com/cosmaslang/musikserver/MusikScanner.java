package com.cosmaslang.musikserver;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusikScanner {

	public interface AudioFileProcessor {
		void processAudioFile(AudioFile audioFile);
	}

	private final AudioFileProcessor processor;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private long count = 0;
	private long failed = 0;

	private final AudioFileFilter audioFileFilter = new AudioFileFilter(false);
	
	public MusikScanner(AudioFileProcessor processor, File rootDirectory) throws FileNotFoundException {
		super();
		this.processor = processor;
		System.out.println("Scanning " + rootDirectory);
		scanDirectory(rootDirectory);
	}

	public void scanDirectory(final File dir) throws FileNotFoundException {
		final File[] files = dir.listFiles(audioFileFilter);
		if (files == null) {
			throw new FileNotFoundException("File not found: " + dir);
		}
		for (File file: files) {
			count++;
			try {
				AudioFile audioFile = AudioFileIO.read(file);
				//logger.info("processed " + file.getName());
				processor.processAudioFile(audioFile);
			} catch (Throwable t) {
				failed++;
				logger.log(Level.WARNING, "Unable to read record:" + count + ":" + file.getPath(), t);
			}
		}

		final File[] audioFileDirs = dir.listFiles(new DirFilter());
		if (audioFileDirs != null) {
			for (File audioFileDir : audioFileDirs) {
				scanDirectory(audioFileDir);
			}
		}
	}
	
	public long getCount() {
		return count;
	}

	public long getFailed() {
		return failed;
	}

	public static final class DirFilter implements java.io.FileFilter {
		public boolean accept(final java.io.File file) {
			return file.isDirectory();
		}
	}

}
