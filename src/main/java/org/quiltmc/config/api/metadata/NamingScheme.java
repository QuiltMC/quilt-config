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

package org.quiltmc.config.api.metadata;

/**
 * A naming scheme to indicate how a string should be formatted.
 * <p>
 * Common formats are exposed in {@link NamingSchemes}.
 * </p>
 * @see org.quiltmc.config.api.annotations.SerializedNameConvention
 */
@FunctionalInterface
public interface NamingScheme {
	String coerce(String input);
}
