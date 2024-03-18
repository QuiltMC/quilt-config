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
