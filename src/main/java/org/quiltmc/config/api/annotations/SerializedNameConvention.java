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
 * Used to tell the serializer what name should be used for this field when saving to disk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface SerializedNameConvention {
	/**
	 * A {@link MetadataType} to supply to {@link Config.Builder#metadata}
	 */
	MetadataType<NamingScheme, SerializedNameConvention.Builder> TYPE = MetadataType.create(Optional::empty, SerializedNameConvention.Builder::new);

	NamingSchemes value() default NamingSchemes.PASSTHROUGH;
	String custom() default "";

	final class Builder implements MetadataType.Builder<NamingScheme> {
		private NamingScheme scheme;

		public Builder() {
			scheme = NamingSchemes.PASSTHROUGH;
		}

		public void set(NamingScheme scheme) {
			if (scheme == NamingSchemes.SPACE_SEPARATED_LOWER_CASE || scheme == NamingSchemes.SPACE_SEPARATED_LOWER_CASE_INITIAL_UPPER_CASE || scheme == NamingSchemes.TITLE_CASE) {
				throw new IllegalArgumentException("Scheme with spaces unsupported for serialized names");
			}
			this.scheme = scheme;
		}

		@Override
		public NamingScheme build() {
			return scheme;
		}
	}
}
