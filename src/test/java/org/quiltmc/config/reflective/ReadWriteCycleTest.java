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

package org.quiltmc.config.reflective;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.TestUtil;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.ConfigsImpl;
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.input.TestReflectiveConfig;

import java.io.IOException;
import java.util.Map;

public class ReadWriteCycleTest {
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
		TestReflectiveConfig config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "tomlTestConfig", TestReflectiveConfig.class);
		setUpConfig(config);

		TestReflectiveConfig readConfig = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "tomlTestConfig", TestReflectiveConfig.class);
		matchConfigs(config, readConfig);
	}

	@Test
	void testJson5ReadWriteCycle() {
		TestReflectiveConfig config = ConfigFactory.create(TestUtil.JSON5_ENV, "testmod", "json5TestConfig", TestReflectiveConfig.class);
		setUpConfig(config);

		TestReflectiveConfig readConfig = ConfigFactory.create(TestUtil.JSON5_ENV, "testmod", "json5TestConfig", TestReflectiveConfig.class);
		matchConfigs(config, readConfig);
	}

	/**
	 * Sets a bunch of nonsense on the config so that it isn't default: if all values were default we wouldn't be able to tell if they were deserialized or not.
	 */
	private void setUpConfig(TestReflectiveConfig config) {
		config.a.setValue(100, true);
		config.nested1.d.setValue(3543897, true);
		config.enabled.value().add("gkjfdhgkjd");
		config.listOfNestedObjects.value().add(ValueMap.builder(0).put("gaming", 123).build());
		config.c.setValue(-2000, true);
		config.whatever.setValue("slaying", true);

		// remove config from internal map to avoid errors when creating the read config
		ConfigsImpl.remove(config);
	}

	/**
	 * Verifies that both metadata and values match in both configs.
	 */
	private void matchConfigs(Config a, Config b) {
		for (ValueTreeNode aNode : a.nodes()) {
			ValueTreeNode bNode = b.getNode(aNode.key());

			for (Map.Entry<MetadataType<?, ?>, Object> metadataEntry : aNode.metadata().entrySet()) {
				if (!bNode.hasMetadata(metadataEntry.getKey())) {
					throw new RuntimeException(String.format("Missing metadata of type '%s' on node '%s'!", metadataEntry.getKey(), aNode.key().toString()));
				} else {
					Object metadata = bNode.metadata().get(metadataEntry.getKey());
					Assertions.assertEquals(metadata, metadataEntry.getValue(), String.format("Metadata mismatch on node %s!", aNode.key().toString()));
					System.out.printf("Matched metadata of type '%s' on key '%s' (%s == %s)%n", metadataEntry.getKey().toString(), aNode.key().toString(), metadata, metadataEntry.getValue().toString());
				}
			}

			if (aNode instanceof TrackedValue<?>)  {
				TrackedValue<?> aValue = a.getValue(aNode.key());
				TrackedValue<?> bValue = b.getValue(aNode.key());

				Assertions.assertEquals(aValue.value(), bValue.value(), String.format("Value mismatch on node %s!", aNode.key().toString()));
				System.out.printf("Matched value on key '%s' (%s == %s)%n", aValue.key().toString(), aValue, bValue);
			}
		}
	}
}
