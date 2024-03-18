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
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.metadata.NamingScheme;
import org.quiltmc.config.api.metadata.NamingSchemes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Used to tell config screen libraries how properties should be formatted when displaying them. Can be applied to configs, sections and properties. {@link DisplayName} must always take priority.
 * @see org.quiltmc.config.api.annotations.DisplayName
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface DisplayNameConvention {
	/**
	 * A {@link MetadataType} to supply to {@link Config.Builder#metadata}
	 */
	MetadataType<NamingScheme, DisplayNameConvention.Builder> TYPE = MetadataType.create(Optional::empty, DisplayNameConvention.Builder::new, true);

	/**
	 * One of the included {@link NamingSchemes}. {@link DisplayNameConvention#custom()} takes priority when not empty
	 * @see NamingSchemes
	 */
	NamingSchemes value() default NamingSchemes.PASSTHROUGH;

	/**
	 * A fully qualified name of a class implementing the {@link NamingScheme}. Ignored when empty, takes precedence over {@link DisplayNameConvention#value()} otherwise.
	 */
	String custom() default "";

	final class Builder implements MetadataType.Builder<NamingScheme> {
		private NamingScheme scheme;

		public Builder() {
			scheme = NamingSchemes.PASSTHROUGH;
		}

		public void set(NamingScheme scheme) {
			this.scheme = scheme;
		}

		@Override
		public NamingScheme build() {
			return scheme;
		}
	}
}
