/*
 * Copyright 2023-2024 QuiltMC
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

package org.quiltmc.config.api.metadata;

import java.security.InvalidParameterException;
import java.util.Objects;

public class DisplayName {
	private final String name;
	private final boolean translatable;

	public DisplayName(String name, boolean translatable) {
		if (name == null || name.isEmpty()) {
			throw new InvalidParameterException("Cannot set serialized name to an empty value!");
		}

		this.translatable = translatable;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isTranslatable() {
		return this.translatable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DisplayName that = (DisplayName) o;
		return this.translatable == that.translatable && Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.translatable);
	}
}
