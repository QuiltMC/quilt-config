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
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.input.TestEscapingConfig;

import java.io.IOException;
import java.nio.file.Files;

public class TestSerializerEscaping extends AbstractConfigTest {
	@Test
	void testTomlReadWriteCycle() throws IOException {
		TestEscapingConfig config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "tomlEscapingTestConfig", TestEscapingConfig.class);
		config.save();

		String content = new String(Files.readAllBytes(TestUtil.TEMP_DIR.resolve("testmod").resolve("tomlEscapingTestConfig.toml")));
		Assertions.assertTrue(content.contains("[\"servers.server.served\"]"), "File contents did not contain proper map key (expected '[\"servers.server.served\"]')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"gaming.awesome\""), "File contents did not contain proper string key (expected '\"gaming.awesome\"')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"cool.awesome@list[]neat\""), "File contents did not contain proper list key (expected '\"cool.awesome@list[]neat\"')!\ncontents:\n" + content);
	}

	@Test
	void testJson5ReadWriteCycle() throws IOException {
		TestEscapingConfig config = ConfigFactory.create(TestUtil.JSON5_ENV, "testmod", "json5EscapingTestConfig", TestEscapingConfig.class);
		config.save();

		String content = new String(Files.readAllBytes(TestUtil.TEMP_DIR.resolve("testmod").resolve("json5EscapingTestConfig.json5")));
		Assertions.assertTrue(content.contains("\"servers.server.served\":"), "File contents did not contain proper map key (expected '\"servers.server.served\":')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"gaming.awesome\":"), "File contents did not contain proper string key (expected '\"gaming.awesome\":')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"cool.awesome@list[]neat\":"), "File contents did not contain proper list key (expected '\"cool.awesome@list[]neat\":')!\ncontents:\n" + content);
	}

	@Test
	void testJsonCReadWriteCycle() throws IOException {
		TestEscapingConfig config = ConfigFactory.create(TestUtil.JSONC_ENV, "testmod", "jsoncEscapingTestConfig", TestEscapingConfig.class);
		config.save();

		String content = new String(Files.readAllBytes(TestUtil.TEMP_DIR.resolve("testmod").resolve("jsoncEscapingTestConfig.jsonc")));
		Assertions.assertTrue(content.contains("\"servers.server.served\":"), "File contents did not contain proper map key (expected '\"servers.server.served\":')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"gaming.awesome\":"), "File contents did not contain proper string key (expected '\"gaming.awesome\":')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"cool.awesome@list[]neat\":"), "File contents did not contain proper list key (expected '\"cool.awesome@list[]neat\":')!\ncontents:\n" + content);
	}

	@Test
	void testJsonReadWriteCycle() throws IOException {
		TestEscapingConfig config = ConfigFactory.create(TestUtil.JSON_ENV, "testmod", "jsonEscapingTestConfig", TestEscapingConfig.class);
		config.save();

		String content = new String(Files.readAllBytes(TestUtil.TEMP_DIR.resolve("testmod").resolve("jsonEscapingTestConfig.json")));
		Assertions.assertTrue(content.contains("\"servers.server.served\":"), "File contents did not contain proper map key (expected '\"servers.server.served\":')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"gaming.awesome\":"), "File contents did not contain proper string key (expected '\"gaming.awesome\":')!\ncontents:\n" + content);
		Assertions.assertTrue(content.contains("\"cool.awesome@list[]neat\":"), "File contents did not contain proper list key (expected '\"cool.awesome@list[]neat\":')!\ncontents:\n" + content);
	}
}
