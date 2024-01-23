/*
 * Copyright 2024 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.config;

import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.api.serializers.Json5Serializer;
import org.quiltmc.config.api.serializers.TomlSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtil {
	public static final Path TEMP_DIR = Paths.get("temp");
	public static final ConfigEnvironment TOML_ENV = new ConfigEnvironment(TestUtil.TEMP_DIR, TomlSerializer.INSTANCE);
	public static final ConfigEnvironment JSON5_ENV = new ConfigEnvironment(TestUtil.TEMP_DIR, Json5Serializer.INSTANCE);

	public static void deleteTempDir() throws IOException {
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
