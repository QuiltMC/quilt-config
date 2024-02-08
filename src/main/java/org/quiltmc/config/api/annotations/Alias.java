package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.metadata.Aliases;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.impl.AliasesImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Used to annotate fields of classes that represent config files with comments that can be saved to disk or displayed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(org.quiltmc.config.impl.Aliases.class)
public @interface Alias {
	/**
	 * A {@link MetadataType} to supply to {@link Config.Builder#metadata}
	 */
	MetadataType<Aliases, Builder> TYPE = MetadataType.create(() -> Optional.of(new AliasesImpl(Collections.emptyList())), Builder::new);

	String[] value();

	final class Builder implements MetadataType.Builder<Aliases> {
		private final List<String> aliases = new ArrayList<>(0);

		public Builder() {
		}

		public void add(String... aliases) {
			for (String alias : aliases) {
				this.aliases.addAll(Arrays.asList(alias.split("\n")));
			}
		}

		@Override
		public Aliases build() {
			return new AliasesImpl(this.aliases);
		}
	}
}

