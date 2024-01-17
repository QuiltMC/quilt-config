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

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;

public final class TestReflectiveConfig2 extends ReflectiveConfig {
	@Comment({"Comment one", "Comment two"})
	public final TrackedValue<Integer> a = this.value(0);
	public final TrackedValue<Integer> b = this.value(1);
	public final TrackedValue<Integer> c = this.value(2);
	public final TrackedValue<Integer> d = this.value(3);
	public final TrackedValue<String> whatever = this.value(null);
	public final Nested nested1 = new Nested();
	public final Nested nested3 = new Nested();
	public final Nested nested4 = new Nested();

	public final TrackedValue<ValueList<ValueMap<Integer>>> listOfNestedObjects = this.list(ValueMap.builder(0).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build()
	);

	public static final class Nested extends Section {
		@SerializedName("custom_serialized_name_a")
		public final TrackedValue<Integer> a = this.value(0);
		public final TrackedValue<Integer> b = this.value(1);
		public final TrackedValue<Integer> c = this.value(2);
		public final TrackedValue<Integer> d = this.value(3);
	}
}
