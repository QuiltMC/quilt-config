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

import java.util.Objects;

public class ChangeWarning {
	private final String customMessage;
	private final Type type;

	public ChangeWarning(String customMessage, Type type) {
		this.customMessage = customMessage;
		this.type = type;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public Type getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ChangeWarning that = (ChangeWarning) o;
		return Objects.equals(customMessage, that.customMessage) && type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(customMessage, type);
	}

	public enum Type {
		/**
		 * indicates that the changed setting requires a restart to apply
		 */
		RequiresRestart,
		/**
		 * indicates that stuff may or will go wrong, and that that is intentional
		 */
		Unsafe,
		/**
		 * indicates that stuff may or will go wrong, because the setting is not mature enough
		 */
		Experimental,
		/**
		 * the message parameter contains the raw message to be displayed
		 */
		CustomTranslatable,
		/**
		 * the message parameter contains the translation key to be displayed
		 */
		Custom
	}
}
