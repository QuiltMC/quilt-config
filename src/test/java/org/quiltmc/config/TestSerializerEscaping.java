package org.quiltmc.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.TestEscapingConfig;

import java.io.IOException;
import java.nio.file.Files;

public class TestSerializerEscaping {
	@BeforeAll
	public static void initializeConfigDir() throws IOException {
		TestUtil.deleteTempDir();
	}

	@AfterAll
	public static void deleteConfigDir() throws IOException {
		TestUtil.deleteTempDir();
	}

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
}
