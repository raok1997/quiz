package com.jana.quiz.utils;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileUtils {
	private static final String TEST_RESOURCES_PATH = System.getProperty("user.dir") + separator + "src" + separator
			+ "test" + separator + "resources" + separator;

	public static String readTestResourceFile(String fileName) throws IOException {
		List<String> lines = Files.readAllLines(getTestResourceFile(fileName).toPath());
		return String.join(System.lineSeparator(), lines);
	}
	
	public static File getTestResourceFile(String fileName) {
		return new File(TEST_RESOURCES_PATH + fileName);
	}
}
