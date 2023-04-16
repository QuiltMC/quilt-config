/*
 * Copyright 2022 QuiltMC
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

package org.quiltmc.config.implementor_api;

import org.quiltmc.config.api.Serializer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ConfigEnvironment {
	private final Map<String, Serializer> serializers = new HashMap<>();
	private final Path saveFolder;
	private final String defaultFileFormat;
	private final String globalSerializer;

	public ConfigEnvironment(Path saveFolder, String globalSerializer, Serializer defaultSerializer, Serializer... serializers) {
		this.serializers.put(defaultSerializer.getFileExtension(), defaultSerializer);
		this.defaultFileFormat = defaultSerializer.getFileExtension();

		for (Serializer serializer : serializers) {
			this.serializers.put(serializer.getFileExtension(), serializer);
		}

		this.saveFolder = saveFolder;
		this.globalSerializer = globalSerializer;
	}

	public ConfigEnvironment(Path saveFolder, Serializer defaultSerializer, Serializer... serializers) {
		this(saveFolder, null, defaultSerializer, serializers);
	}

	public Path getSaveDir() {
		return this.saveFolder;
	}

	public String getDefaultFormat() {
		return this.defaultFileFormat;
	}

	public String getGlobalFormat() {
		return globalSerializer;
	}

	public Serializer registerSerializer(Serializer serializer) {
		return serializers.put(serializer.getFileExtension(), serializer);
	}

	public Serializer getActualSerializer(String fileType) {
		if (this.serializers.containsKey(fileType)) {
			return this.serializers.get(fileType);
		} else {
			throw new RuntimeException("No serializer registered for extension '." + fileType + "'");
		}
	}

	public Serializer getSerializer(String fileType) {
		return getActualSerializer(this.globalSerializer == null ? fileType : this.globalSerializer);
	}
}
