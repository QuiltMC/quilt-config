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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.TestUtil;
import org.quiltmc.config.impl.util.ConfigsImpl;
import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.input.TestReflectiveConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AliasTest extends AbstractConfigTest {
	@Test
	void testToml() throws IOException {
		test(TestUtil.TOML_ENV);
	}

	@Test
	void testJson() throws IOException {
		test(TestUtil.JSON5_ENV);
	}

	private static void test(ConfigEnvironment env) throws IOException {
		ConfigsImpl.removeAll();

		// set "a" to some nonsense
		TestReflectiveConfig config1 = ConfigFactory.create(env, "testmod", "testConfig", TestReflectiveConfig.class);
		config1.a.setValue(3005);
		ConfigsImpl.removeAll();

		// change key of "a" (serialized as "george") to "willy"
		Path configPath = TestUtil.TEMP_DIR.resolve("testmod/testConfig." + env.getDefaultFormat());
		byte[] bytes = Files.readAllBytes(configPath);
		String fileContent = new String(bytes);
		fileContent = fileContent.replace("george", "willy");
		Files.write(configPath, fileContent.getBytes());

		// recheck a
		TestReflectiveConfig config2 = ConfigFactory.create(env, "testmod", "testConfig", TestReflectiveConfig.class);
		Assertions.assertEquals(3005, config2.a.value());
		ConfigsImpl.removeAll();

		// reset a
		config2.a.setValue(200006);

		// check if the key has returned to "george"
		byte[] newBytes = Files.readAllBytes(configPath);
		String newContent = new String(newBytes);
		if (!newContent.contains("george")) {
			throw new RuntimeException("key was serialized as an alias instead of the serialized name!");
		}

		//set the key to alias #2, "johnson"
		newContent = newContent.replace("george", "johnson");
		Files.write(configPath, newContent.getBytes());

		// recheck a
		TestReflectiveConfig config3 = ConfigFactory.create(env, "testmod", "testConfig", TestReflectiveConfig.class);
		Assertions.assertEquals(200006, config3.a.value());
	}
}
