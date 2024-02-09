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
		newContent = newContent.replace("george", "willy");
		Files.write(configPath, newContent.getBytes());

		// recheck a
		TestReflectiveConfig config3 = ConfigFactory.create(env, "testmod", "testConfig", TestReflectiveConfig.class);
		Assertions.assertEquals(200006, config3.a.value());
	}
}
