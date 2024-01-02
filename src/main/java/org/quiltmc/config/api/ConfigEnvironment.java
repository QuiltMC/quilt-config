/*
 * Copyright 2023-2024 QuiltMC
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

package org.quiltmc.config.api;

import java.nio.file.Path;

/**
 * @deprecated Go through your implementor's API, or use {@link org.quiltmc.config.implementor_api.ConfigEnvironment}
 * if necessary.
 */
@Deprecated
public final class ConfigEnvironment extends org.quiltmc.config.implementor_api.ConfigEnvironment {
	public ConfigEnvironment(Path saveFolder, String globalSerializer, Serializer defaultSerializer, Serializer... serializers) {
		super(saveFolder, globalSerializer, defaultSerializer, serializers);
	}

	public ConfigEnvironment(Path saveFolder, Serializer defaultSerializer, Serializer... serializers) {
		this(saveFolder, null, defaultSerializer, serializers);
	}

	public Path getSaveDir() {
		return super.getSaveDir();
	}

	public String getDefaultFormat() {
		return super.getDefaultFormat();
	}

	public String getGlobalFormat() {
		return super.getGlobalFormat();
	}

	public Serializer registerSerializer(Serializer serializer) {
		return super.registerSerializer(serializer);
	}

	public Serializer getActualSerializer(String fileType) {
		return super.getActualSerializer(fileType);
	}

	public Serializer getSerializer(String fileType) {
		return super.getSerializer(fileType);
	}
}
