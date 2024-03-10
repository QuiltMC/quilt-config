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

package org.quiltmc.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.api.metadata.NamingSchemes;

import java.util.HashMap;
import java.util.Map;

public class NamingSchemeTest {
	public static final String[] inputs = new String[] {
		"theQuickBrownFoxJumpsOverTheLazyDog",
		"The quick brown fox jumps over the lazy dog",
		"TheQuickBrownFoxJumpsOverTheLazyDog",
		"THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG",
		"the_quick_brown_fox_jumps_over_the_lazy_dog",
	};

	public static final Map<NamingSchemes, String> namingSchemesToResults = new HashMap<>();
	static {
		namingSchemesToResults.put(NamingSchemes.KEBAB_CASE, "the-quick-brown-fox-jumps-over-the-lazy-dog");
		namingSchemesToResults.put(NamingSchemes.SNAKE_CASE, "the_quick_brown_fox_jumps_over_the_lazy_dog");
		namingSchemesToResults.put(NamingSchemes.LOWER_CAMEL_CASE, "theQuickBrownFoxJumpsOverTheLazyDog");
		namingSchemesToResults.put(NamingSchemes.UPPER_CAMEL_CASE, "TheQuickBrownFoxJumpsOverTheLazyDog");
		namingSchemesToResults.put(NamingSchemes.SPACE_SEPARATED_LOWER_CASE, "the quick brown fox jumps over the lazy dog");
		namingSchemesToResults.put(NamingSchemes.SPACE_SEPARATED_LOWER_CASE_INITIAL_UPPER_CASE, "The quick brown fox jumps over the lazy dog");
		namingSchemesToResults.put(NamingSchemes.TITLE_CASE, "The Quick Brown Fox Jumps Over The Lazy Dog");
	}

	@Test
	public void testNamingSchemeConversions() {
		for (String input: inputs) {
			Assertions.assertEquals(input, NamingSchemes.PASSTHROUGH.coerce(input));
			for (Map.Entry<NamingSchemes, String> namingSchemesStringEntry : namingSchemesToResults.entrySet()) {
				Assertions.assertEquals(namingSchemesStringEntry.getValue(), namingSchemesStringEntry.getKey().coerce(input));
			}
		}
	}

}
