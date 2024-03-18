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

package org.quiltmc.config.impl.util;

import org.quiltmc.config.api.annotations.DisplayNameConvention;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingScheme;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class NamingSchemeHelper {
	private final ClassLoader classLoader;
	private final Map<String, NamingScheme> customSchemeCache;

	public NamingSchemeHelper(ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.customSchemeCache = new HashMap<>();
	}

	public NamingSchemeHelper() {
		this(NamingSchemeHelper.class.getClassLoader());
	}

	public NamingScheme getNamingScheme(SerializedNameConvention annotation, BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
		if (annotation.custom().isEmpty()) {
			return annotation.value();
		} else {
			return this.createCustomNamingScheme(annotation.custom(), exceptionFactory);
		}
	}

	public NamingScheme getNamingScheme(DisplayNameConvention annotation, BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
		if (annotation.custom().isEmpty()) {
			return annotation.value();
		} else {
			return createCustomNamingScheme(annotation.custom(), exceptionFactory);
		}
	}

	private NamingScheme createCustomNamingScheme(String className, BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
		return this.customSchemeCache.computeIfAbsent(className, customSchemeName -> {
			NamingScheme customScheme;
			try {
				Class<?> customSchemeClass = Class.forName(customSchemeName, true, this.classLoader);
				customScheme = (NamingScheme) customSchemeClass.newInstance();
			} catch (ClassNotFoundException e) {
				throw exceptionFactory.apply("Couldn't find custom naming scheme class '" + customSchemeName + "'", e);
			} catch (InstantiationException | IllegalAccessException e) {
				throw exceptionFactory.apply("Couldn't create instance of custom name scheme class '" + customSchemeName + "'", e);
			} catch (ClassCastException e) {
				throw exceptionFactory.apply("Class '" + customSchemeName + "' does not implement '" + NamingScheme.class.getName() + "'", e);
			}

			return customScheme;
		});
	}
}
