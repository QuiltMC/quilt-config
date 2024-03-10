/*
 * Copyright 2022-2024 QuiltMC
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

package org.quiltmc.config.impl.tree;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.ValueKey;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.AbstractMetadataContainer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SectionTreeNode extends AbstractMetadataContainer implements ValueTreeNode.Section {
	private final Trie.Node node;

	public SectionTreeNode(Trie.Node node, Map<MetadataType<?, ?>, Object> metadata) {
		super(metadata);
		this.node = node;
	}

	@Override
	public ValueKey key() {
		return this.node.getKey();
	}

	@Override
	public void propagateInheritedMetadata(Map<MetadataType<?, ?>, Object> inheritedMetadata) {
		for (Map.Entry<MetadataType<?,?>, Object> entry: inheritedMetadata.entrySet()) {
			this.metadata.putIfAbsent(entry.getKey(), entry.getValue());
		}
		Map<MetadataType<?, ?>, Object> inheritorMetadata = new LinkedHashMap<>();
		for (Map.Entry<MetadataType<?, ?>, Object> entry: metadata.entrySet()) {
			if (entry.getKey().isInherited()) {
				inheritorMetadata.put(entry.getKey(), entry.getValue());
			}
		}
		for (ValueTreeNode node : this) {
			node.propagateInheritedMetadata(inheritorMetadata);
		}
	}

	@NotNull
	@Override
	public Iterator<ValueTreeNode> iterator() {
		return new Iterator<ValueTreeNode>() {
			private final Iterator<Trie.Node> itr = SectionTreeNode.this.node.iterator();

			@Override
			public boolean hasNext() {
				return itr.hasNext();
			}

			@Override
			public ValueTreeNode next() {
				return itr.next().getValue();
			}
		};
	}
}
