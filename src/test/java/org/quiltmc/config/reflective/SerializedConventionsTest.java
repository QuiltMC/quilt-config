/*
 * Copyright 2025 QuiltMC
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
import org.quiltmc.config.reflective.input.TestConventionConfig;

/**
 * Tests <a href="https://github.com/QuiltMC/quilt-config/pull/58">the JSON serializer failing to apply serialized names on section values</a>.
 * Note: this is also tested by {@link ReadWriteCycleTest}, but this test is left in for increased granularity.
 */
public class SerializedConventionsTest extends AbstractConfigTest {
	@Test
	void testToml() {
		test(TestUtil.TOML_ENV);
	}

	@Test
	void testJson5() {
		test(TestUtil.JSON5_ENV);
	}

	@Test
	void testJsonC() {
		test(TestUtil.JSONC_ENV);
	}

	@Test
	void testJson() {
		test(TestUtil.JSON_ENV);
	}

	private static void test(ConfigEnvironment env) {
		TestConventionConfig config = ConfigFactory.create(env, "testmod", "testConventionConfig", TestConventionConfig.class);
		config.word.angryBee.setValue(50);
		config.sectionWithALotOfWords.eclasticFieryGuarana.setValue(10);
		config.save();

		ConfigsImpl.removeAll();

		TestConventionConfig readConfig = ConfigFactory.create(env, "testmod", "testConventionConfig", TestConventionConfig.class);

		Assertions.assertEquals(50, readConfig.word.angryBee.value());
		Assertions.assertEquals(10, readConfig.sectionWithALotOfWords.eclasticFieryGuarana.value());
	}
}
