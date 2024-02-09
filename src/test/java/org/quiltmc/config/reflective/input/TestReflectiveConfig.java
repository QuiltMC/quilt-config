/*
 * Copyright 2022-2024 QuiltMC
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

import org.quiltmc.config.Vec3i;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.ConfigIgnored;
import org.quiltmc.config.api.annotations.IntegerRange;
import org.quiltmc.config.api.annotations.Matches;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;

/**
 * A test config designed to use absolutely every single possible feature.
 */
@SuppressWarnings("unused")
@Processor("processConfig")
public final class TestReflectiveConfig extends ReflectiveConfig {
	@Comment({"Comment one", "Comment two"})
	@SerializedName("george")
	public final TrackedValue<Integer> a = this.value(0);

	// this value would throw an exception for being non-final if it was considered by the config
	// if tests pass, this annotation is working!
	@ConfigIgnored
	public Integer gaming = 4897;

	@Comment("Comment one")
	@Comment("Comment two")
	public final TrackedValue<Integer> b = this.value(1);
	public final TrackedValue<Integer> c = this.value(2);

	@IntegerRange(min=0, max=10)
	public final TrackedValue<Integer> d = this.value(3);
	@SerializedName("custom_serialized_name_vec")
	public final TrackedValue<Vec3i> vec = this.value(new Vec3i(100, 200, 300));

	@Matches("[a-zA-Z]+")
	public final TrackedValue<String> whatever = this.value("Riesling");
	@SerializedName("reallyAwesomeNested")
	@Comment("comment!")
	public final Nested nested1 = new Nested();
	@Processor("processSection")
	public final Nested nested3 = new Nested();

	public final TrackedValue<ValueList<String>> enabled = this.list("");

	@Processor("processField")
	public final TrackedValue<ValueList<Vec3i>> vecs = this.list(new Vec3i(0, 0, 0),
			new Vec3i(1, 2, 3),
			new Vec3i(4, 5, 6),
			new Vec3i(7, 8, 9)
	);

	@Comment("Test section comment 1")
	@Comment("Test section comment 2")
	@Comment("Test section comment 3")
	@Comment("Test section comment 4")
	public final Nested nested4 = new Nested();

	public final TrackedValue<ValueMap<ValueList<String>>> key_binds = this.map(ValueList.create("")).build();

	@IntegerRange(min=0, max=10)
	public final TrackedValue<ValueList<Integer>> ints = this.list(0, 1, 2, 3, 4);

	public final TrackedValue<ValueList<ValueMap<Integer>>> listOfNestedObjects = this.list(ValueMap.builder(0).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build()
	);

	public void processConfig(Config.Builder builder) {
		System.out.println("Processing config!");
	}

	public void processField(TrackedValue.Builder<Vec3i> process) {
		System.out.println("Processing field!");
	}

	public void processSection(SectionBuilder builder) {
		System.out.println("Processing section!");
	}

	public static final class Nested extends Section {
		@SerializedName("custom_serialized_name_a")
		public final TrackedValue<Integer> a = this.value(0);
		public final TrackedValue<Integer> b = this.value(1);
		public final TrackedValue<Integer> c = this.value(2);
		public final TrackedValue<Integer> d = this.value(3);
	}
}
