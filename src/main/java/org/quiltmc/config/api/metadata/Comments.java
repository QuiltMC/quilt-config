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

package org.quiltmc.config.api.metadata;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.values.TrackedValue;

/**
 * A list of strings that is used by {@link org.quiltmc.config.api.annotations.Comment} as a container for any
 * number of comments that might be associated with a given {@link TrackedValue} or {@link Config}
 */
public interface Comments extends Iterable<String> {
}
