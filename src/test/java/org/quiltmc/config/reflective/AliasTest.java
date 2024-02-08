package org.quiltmc.config.reflective;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.TestUtil;
import org.quiltmc.config.impl.util.ConfigsImpl;
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.input.TestReflectiveConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AliasTest extends AbstractConfigTest {
	@Test
	void testToml() throws IOException {
		// set "a" to some nonsense
		TestReflectiveConfig config1 = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "tomlAliasTestConfig", TestReflectiveConfig.class);
		config1.a.setValue(3005, true);
		ConfigsImpl.remove(config1);

		// change key of "a" (serialized as "george") to "willy"
		Path configPath = TestUtil.TEMP_DIR.resolve("testmod/tomlAliasTestConfig.toml");
		byte[] bytes = Files.readAllBytes(configPath);
		String fileContent = new String(bytes);
		fileContent = fileContent.replace("george", "willy");
		Files.write(configPath, fileContent.getBytes());

		// recheck a
		TestReflectiveConfig config2 = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "tomlAliasTestConfig", TestReflectiveConfig.class);
		Assertions.assertEquals(3005, config2.a.value());
	}
}
