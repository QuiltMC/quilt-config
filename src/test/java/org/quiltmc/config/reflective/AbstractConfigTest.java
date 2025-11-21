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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.quiltmc.config.TestUtil;
import org.quiltmc.config.impl.util.ConfigsImpl;

import java.io.IOException;

public abstract class AbstractConfigTest {
	@BeforeEach
	public void initialize() throws IOException {
		TestUtil.deleteTempDir();
		ConfigsImpl.removeAll();
	}

	@AfterEach
	public void cleanUp() throws IOException {
		TestUtil.deleteTempDir();
		ConfigsImpl.removeAll();
	}
}
