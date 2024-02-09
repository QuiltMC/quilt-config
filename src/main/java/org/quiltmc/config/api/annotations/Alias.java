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

package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.metadata.Aliases;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.impl.AliasesImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a past alias of this field's name.
 * A field's aliases will be considered during deserialization, but the field will be saved as its name or {@link SerializedName serial name} when being serialized.
 * This is useful for migrating old configs to newer names.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(org.quiltmc.config.impl.Aliases.class)
public @interface Alias {
	/**
	 * A {@link MetadataType} to supply to {@link Config.Builder#metadata}
	 */
	MetadataType<Aliases, Builder> TYPE = MetadataType.create(() -> Optional.of(new AliasesImpl(Collections.emptyList())), Builder::new);

	String[] value();

	final class Builder implements MetadataType.Builder<Aliases> {
		private final List<String> aliases = new ArrayList<>(0);

		public Builder() {
		}

		public void add(String... aliases) {
			this.aliases.addAll(Arrays.asList(aliases));
		}

		@Override
		public Aliases build() {
			return new AliasesImpl(this.aliases);
		}
	}
}

