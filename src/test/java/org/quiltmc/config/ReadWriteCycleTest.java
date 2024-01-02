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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.serializer.Json5Serializer;
import org.quiltmc.config.api.serializer.TomlSerializer;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.ConfigsImpl;
import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.TestReflectiveConfig;

import java.io.IOException;
import java.util.Map;

public class ReadWriteCycleTest {
	private static final ConfigEnvironment TOML_ENV = new ConfigEnvironment(TestUtil.TEMP_DIR, TomlSerializer.INSTANCE);
	private static final ConfigEnvironment JSON5_ENV = new ConfigEnvironment(TestUtil.TEMP_DIR, Json5Serializer.INSTANCE);

	@BeforeAll
	public static void initializeConfigDir() throws IOException {
		TestUtil.deleteTempDir();
	}

	@AfterAll
	public static void deleteConfigDir() throws IOException {
		TestUtil.deleteTempDir();
	}

	@Test
	void testTomlReadWriteCycle() {
		TestReflectiveConfig config = ConfigFactory.create(TOML_ENV, "testmod", "tomlTestConfig", TestReflectiveConfig.class);
		setUpConfig(config);

		TestReflectiveConfig readConfig = ConfigFactory.create(TOML_ENV, "testmod", "tomlTestConfig", TestReflectiveConfig.class);
		matchConfigs(config, readConfig);
	}

	@Test
	void testJson5ReadWriteCycle() {
		TestReflectiveConfig config = ConfigFactory.create(JSON5_ENV, "testmod", "json5TestConfig", TestReflectiveConfig.class);
		setUpConfig(config);

		TestReflectiveConfig readConfig = ConfigFactory.create(JSON5_ENV, "testmod", "json5TestConfig", TestReflectiveConfig.class);
		matchConfigs(config, readConfig);
	}

	// todo set more nonsense
	/**
	 * Sets a bunch of nonsense on the config so that it isn't default: if all values were default we wouldn't be able to tell if they were deserialized or not.
	 */
	private void setUpConfig(TestReflectiveConfig config) {
		config.a.setValue(100, true);

		// remove config from internal map to avoid errors when creating the read config
		ConfigsImpl.remove(config);
	}

	// todo include more info in errors
	/**
	 * Verifies that both metadata and values match in both configs.
	 */
	private void matchConfigs(Config a, Config b) {
		for (ValueTreeNode aNode : a.nodes()) {
			ValueTreeNode bNode = b.getNode(aNode.key());

			for (Map.Entry<MetadataType<?, ?>, Object> metadataEntry : aNode.metadata().entrySet()) {
				if (!bNode.hasMetadata(metadataEntry.getKey())) {
					throw new RuntimeException("Missing metadata: " + metadataEntry.getKey());
				} else {
					Object metadata = bNode.metadata().get(metadataEntry.getKey());
					if (!metadata.equals(metadataEntry.getValue())) {
						throw new RuntimeException("Non-equal metadata: " + metadataEntry.getValue());
					}
				}
			}

			if (aNode instanceof TrackedValue<?>)  {
				TrackedValue<?> aValue = a.getValue(aNode.key());
				TrackedValue<?> bValue = b.getValue(aNode.key());

				Assertions.assertEquals(aValue.value(), bValue.value());
			}
		}
	}
}
