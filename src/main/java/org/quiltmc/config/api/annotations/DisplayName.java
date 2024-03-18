package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.metadata.MetadataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Used to tell config screen libraries what name should be used for the annotated element when displaying it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface DisplayName {
	/**
	 * A {@link MetadataType} to supply to {@link Config.Builder#metadata}
	 */
	MetadataType<org.quiltmc.config.api.metadata.DisplayName, DisplayName.Builder> TYPE = MetadataType.create(
		Optional::empty,
		DisplayName.Builder::new
	);

	/**
	 * The name for the config screen to use if {@link #translatable} is false. A translation key pointing to the name to be used otherwise
	 */
	String value();

	/**
	 * If true, {@link #value()} contains a translation key
	 */
	boolean translatable() default false;

	final class Builder implements MetadataType.Builder<org.quiltmc.config.api.metadata.DisplayName> {
		private String name;
		private boolean translatable;

		public Builder() {
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setTranslatable(boolean translatable) {
			this.translatable = translatable;
		}

		@Override
		public org.quiltmc.config.api.metadata.DisplayName build() {
			return new org.quiltmc.config.api.metadata.DisplayName(this.name, this.translatable);
		}
	}
}
