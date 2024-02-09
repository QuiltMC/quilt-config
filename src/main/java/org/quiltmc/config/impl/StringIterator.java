package org.quiltmc.config.impl;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.impl.util.ImmutableIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StringIterator implements Iterable<String> {
	private final List<String> strings;

	public StringIterator(List<String> strings) {
		this.strings = new ArrayList<>(strings);
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return new ImmutableIterable<>(this.strings).iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StringIterator iterator = (StringIterator) o;
		return Objects.equals(this.strings, iterator.strings);
	}

	@Override
	public int hashCode() {
		return Objects.hash(strings);
	}
}
