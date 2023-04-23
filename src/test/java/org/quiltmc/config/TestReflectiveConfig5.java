/*
 * Copyright 2023 QuiltMC
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

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.NameConvention;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.naming.NamingSchemes;
import org.quiltmc.config.api.values.TrackedValue;

@NameConvention(NamingSchemes.SNAKE_CASE)
public final class TestReflectiveConfig5 extends ReflectiveConfig {
    public final TrackedValue<String> sampleText = value("haha yes");

    @NameConvention(NamingSchemes.LOWER_CASE_DASHED)
    public final TrackedValue<Boolean> differentConvention = value(true);

    @SerializedName("a_completely_different_name")
    public final TrackedValue<Integer> someName = value(24);

    public final Nested differentConventionForSection = new Nested();

	public final SnakeNested snakeNested = new SnakeNested();

    @NameConvention(NamingSchemes.CAMEL_CASE)
    public final Nested overridenConventionForSection = new Nested();

    @NameConvention(custom = "org.quiltmc.config.ScreamingSnakeCaseNamingScheme")
    public static final class Nested extends ReflectiveConfig.Section {
        public final TrackedValue<String> hello = value("string");
        public final TrackedValue<Boolean> customConvention = value(false);
		@NameConvention(NamingSchemes.LOWER_CASE_DASHED)
		public final TrackedValue<String> alwaysKebab = value("Poop!");
		@SerializedName("An2-!!hbDA")
		public final TrackedValue<String> overriden = value("Poop2!");
    }

	// note no annotation, should inherit snake case from parent
	public static final class SnakeNested extends ReflectiveConfig.Section {
		public final TrackedValue<Vec3i> vectorFromThatOneMovie = value(new Vec3i(1, 2, 3));
		public final TrackedValue<Float> floatingAway = value(2.0f);
	}
}
