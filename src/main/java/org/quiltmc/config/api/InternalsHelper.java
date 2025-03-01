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

package org.quiltmc.config.api;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.builders.ReflectiveConfigCreator;

/**
 * Provides the implementation packages access to package-private methods. <strong>DO NOT USE THIS CLASS</strong>
 */

@ApiStatus.Internal
public final class InternalsHelper {
	private InternalsHelper() {
	}

	public static <T extends ReflectiveConfig> void setWrappedConfig(T wrapped, Config config) {
		wrapped.setWrappedConfig(config);
		for (ValueTreeNode node : config.nodes()) {
			if (node instanceof ValueTreeNode.Section) {
				ValueTreeNode.Section section = (ValueTreeNode.Section) node;
				ReflectiveConfigCreator.Reflective marker = section.metadata(ReflectiveConfigCreator.Reflective.TYPE);
				if (marker != null) {
					marker.self.setWrappedSection(section);
				}
			}
		}
	}
}
