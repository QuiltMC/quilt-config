package org.quiltmc.config.reflective;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.quiltmc.config.TestUtil;
import org.quiltmc.config.impl.util.ConfigsImpl;

import java.io.IOException;

public abstract class AbstractConfigTest {
	@BeforeAll
	public static void initialize() throws IOException {
		TestUtil.deleteTempDir();
		ConfigsImpl.removeAll();
	}

	@AfterAll
	public static void cleanUp() throws IOException {
		TestUtil.deleteTempDir();
		ConfigsImpl.removeAll();
	}
}
