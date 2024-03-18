package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.metadata.MetadataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Used to tell config screen libraries that a warning should be displayed before applying changes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ChangeWarning {
	/**
	 * A {@link MetadataType} to supply to {@link Config.Builder#metadata}
	 */
	MetadataType<org.quiltmc.config.api.metadata.ChangeWarning, ChangeWarning.Builder> TYPE = MetadataType.create(Optional::empty, ChangeWarning.Builder::new, true);

	/**
	 * The {@link org.quiltmc.config.api.metadata.ChangeWarning.Type ChangeWarning.Type} of the change warning
	 */
	org.quiltmc.config.api.metadata.ChangeWarning.Type value();

	/**
	 * The message to display if the type is {@link org.quiltmc.config.api.metadata.ChangeWarning.Type#Custom ChangeWarning.Type.Custom}, or the translation key if it is {@link org.quiltmc.config.api.metadata.ChangeWarning.Type#CustomTranslatable ChangeWarning.Type.CustomTranslatable}
	 */
	String customMessage() default "";


	final class Builder implements MetadataType.Builder<org.quiltmc.config.api.metadata.ChangeWarning> {
		private String message;
		private org.quiltmc.config.api.metadata.ChangeWarning.Type type;

		public Builder() {
		}

		/**
		 * Utility for updating the type and the message
		 */
		public void setMessage(String message) {
			this.setType(org.quiltmc.config.api.metadata.ChangeWarning.Type.Custom);
			this.message = message;
		}

		public void setType(org.quiltmc.config.api.metadata.ChangeWarning.Type type) {
			this.type = type;
		}

		/**
		 * Utility for updating the type and the message
		 */
		public void setTranslatableMessage(String translationKey) {
			this.setType(org.quiltmc.config.api.metadata.ChangeWarning.Type.CustomTranslatable);
			this.setMessage(translationKey);
		}

		@Override
		public org.quiltmc.config.api.metadata.ChangeWarning build() {
			return new org.quiltmc.config.api.metadata.ChangeWarning(this.message, this.type);
		}
	}
}
