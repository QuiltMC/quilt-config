package org.quiltmc.config.impl;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.metadata.Aliases;
import org.quiltmc.config.impl.util.ImmutableIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class AliasesImpl implements Aliases {
	private final List<String> aliases;

	public AliasesImpl(List<String> aliases) {
		this.aliases = new ArrayList<>(aliases);
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return new ImmutableIterable<>(this.aliases).iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AliasesImpl strings = (AliasesImpl) o;
		return Objects.equals(aliases, strings.aliases);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aliases);
	}
}
