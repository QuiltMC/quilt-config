/*
 * Copyright 2022 QuiltMC
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

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;

public final class TestReflectiveConfig2 extends ReflectiveConfig {
	@Comment({"Comment one", "Comment two"})
	public final TrackedValue<Integer> a = value(0);
	public final TrackedValue<Integer> b = value(1);
	public final TrackedValue<Integer> c = value(2);
	public final TrackedValue<Integer> d = value(3);
	public final TrackedValue<String> whatever = null;
	public final Nested nested1 = new Nested(10, 11, 12, 13);
	public final Nested nested3 = new Nested(20, 21, 22, 23);
	public final Nested nested4 = new Nested(30, 31, 32, 33);

	public final TrackedValue<ValueList<ValueMap<Integer>>> listOfNestedObjects = list(ValueMap.builder(0).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build(),
			ValueMap.builder(0).put("a", 1).put("b", 2).put("c", 3).put("d", 4).build()
	);

	public static final class Nested extends Section {
		public final TrackedValue<Integer> a;
		public final TrackedValue<Integer> b;
		public final TrackedValue<Integer> c;
		public final TrackedValue<Integer> d;

		public Nested(int a, int b, int c, int d) {
			this.a = value(a);
			this.b = value(b);
			this.c = value(c);
			this.d = value(d);
		}
	}

}
