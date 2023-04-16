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
