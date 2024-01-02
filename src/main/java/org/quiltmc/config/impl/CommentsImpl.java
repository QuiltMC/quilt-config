/*
 * Copyright 2022-2023 QuiltMC
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

package org.quiltmc.config.impl;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.metadata.Comments;
import org.quiltmc.config.impl.util.ImmutableIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class CommentsImpl implements Comments {
	private final List<String> comments;

	public CommentsImpl(List<String> comments) {
		this.comments = new ArrayList<>(comments);
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return new ImmutableIterable<>(this.comments).iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommentsImpl strings = (CommentsImpl) o;
		return Objects.equals(comments, strings.comments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(comments);
	}
}
