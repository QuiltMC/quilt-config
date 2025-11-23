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

package org.quiltmc.config.reflective.input;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingSchemes;
import org.quiltmc.config.api.values.TrackedValue;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class TestConventionConfig extends ReflectiveConfig {
	public final Word word = new Word();
	public final SectionWithALotOfWords sectionWithALotOfWords = new SectionWithALotOfWords();

	public static final class Word extends Section {
		public final TrackedValue<Integer> angryBee = this.value(100);
	}

	public static final class SectionWithALotOfWords extends Section {
		public final TrackedValue<Integer> curiousDice = this.value(123);
		public final TrackedValue<Integer> eclasticFieryGuarana = this.value(321);
	}
}
