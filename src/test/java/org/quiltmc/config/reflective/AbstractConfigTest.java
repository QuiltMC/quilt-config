package org.quiltmc.config.reflective;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.quiltmc.config.TestUtil;

import java.io.IOException;

public abstract class AbstractConfigTest {
	@BeforeAll
	public static void initializeConfigDir() throws IOException {
		TestUtil.deleteTempDir();
	}

	@AfterAll
	public static void deleteConfigDir() throws IOException {
		TestUtil.deleteTempDir();
	}
}
