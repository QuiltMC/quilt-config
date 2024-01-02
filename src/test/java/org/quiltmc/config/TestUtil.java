package org.quiltmc.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtil {
	static final Path TEMP_DIR = Paths.get("temp");

	static void deleteTempDir() throws IOException {
		deleteDirectoryRecursively(TEMP_DIR.toFile());
	}

	private static void deleteDirectoryRecursively(File toBeDeleted) throws IOException {
		File[] allContents = toBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectoryRecursively(file);
			}
		}

		Files.deleteIfExists(toBeDeleted.toPath());
	}
}
