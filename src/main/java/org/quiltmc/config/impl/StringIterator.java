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

package org.quiltmc.config.impl;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.impl.util.ImmutableIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StringIterator implements Iterable<String> {
	private final List<String> strings;

	public StringIterator(List<String> strings) {
		this.strings = new ArrayList<>(strings);
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return new ImmutableIterable<>(this.strings).iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StringIterator iterator = (StringIterator) o;
		return Objects.equals(this.strings, iterator.strings);
	}

	@Override
	public int hashCode() {
		return Objects.hash(strings);
	}
}
