/*
 * Copyright 2022 QuiltMC
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

package org.quiltmc.config.api;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.config.api.metadata.MetadataContainer;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.*;
import org.quiltmc.config.impl.tree.TrackedValueImpl;
import org.quiltmc.config.impl.util.ConfigUtils;

import java.nio.file.Path;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface Config extends MetadataContainer {
	/**
	 * The ID of the config family this config file belongs to, also the folder the resulting file will be saved in.
	 */
	String family();

	/**
	 * The unique ID of this config file, also the name of the resulting file.
	 */
	String id();

	/**
	 * The path this config will be saved in, relative to the root config directory.
	 *
	 * @return a save path
	 */
	Path savePath();

	/**
	 * Adds a listener to this {@link Config} that's called whenever any of its values are updated
	 *
	 * @param callback an update listener
	 */
	void registerCallback(UpdateCallback callback);

	/**
	 * @return the metadata attached to this value for the specified type
	 */
	<M> M metadata(MetadataType<M, ?> type);

	/**
	 * @return whether or not this value has any metadata of the specified type
	 */
	<M> boolean hasMetadata(MetadataType<M, ?> type);

	/**
	 * Serialize this config and all its values to disk
	 */
	void save();

	/**
	 * Returns all values held by this config file
	 *
	 * <p>For all nodes, including section nodes, see {@link #nodes}
	 *
	 * @return all values held by this config file
	 */
	Iterable<TrackedValue<?>> values();

	/**
	 * @param key an iterable of key components that make up a {@link TrackedValue}'s {@link ValueKey}
	 * @return the value contained by this config class
	 */
	TrackedValue<?> getValue(Iterable<String> key);

	/**
	 * Returns all top-level nodes of the value tree represented by this config file, including section nodes
	 *
	 * <p>Consider a config represented by the following JSON5 file:
	 * <pre>
	 * {
	 *     wumbo: "mayonnaise",
	 *     gui: {
	 *         scale: 1.0,
	 *         fg_color: "0xFFFFFFFF",
	 *         bg_color: "0x80000000
	 *     },
	 *     count: 100
	 * }</pre>
	 *
	 * iterating over the nodes in the tree would produce the following nodes:
	 * <ul>
	 *     <li>TrackedValue("wumbo", "mayonnaise")</li>
	 *     <li>ValueTreeNode.Section("gui")</li>
	 *     <li>TrackedValue("count", 100)</li>
	 * </ul>
	 *
	 * iterating over children would need to be done by checking <pre>node instanceof ValueTreeNode.Section<pre>
	 * and iterating over that node as well, recursively.
	 */
	Iterable<ValueTreeNode> nodes();

	interface UpdateCallback {
		void onUpdate(Config config);
	}

	interface Creator {
		void create(Builder builder);
	}

	@ApiStatus.NonExtendable
	interface Builder extends SectionBuilder {
		/**
		 * Adds a value to this config file
		 *
		 * A field should be either:
		 * <ul>
		 *     <li>A basic type (int, long, float, double, boolean, String, or enum)</li>
		 *     <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types)</li>
		 * </ul>
		 * @return this
		 */
		Builder field(TrackedValue<?> value);

		/**
		 * Creates a new section nested within this config file
		 *
		 * @return this
		 */
		Builder section(String key, Consumer<SectionBuilder> creator);

		/**
		 * Create or configure a piece of metadata
		 *
		 * @param type the type of metadata to configure
		 * @param builderConsumer the modifications to be made to the piece of metadata
		 * @return this
		 */
		<M, B extends MetadataType.Builder<M>> Builder metadata(MetadataType<M, B> type, Consumer<B> builderConsumer);

		/**
		 * Adds a default listener to the resulting {@link Config} that's called whenever any of its values updated
		 *
		 * @param callback an update listener
		 * @return this
		 */
		Builder callback(UpdateCallback callback);

		/**
		 * Sets the default file type for the config file this config will be saved to
		 *
		 * Note that this can be overridden by the end user with a launch parameter
		 *
		 * @return this
		 */
		Builder format(String format);
	}

	@ApiStatus.NonExtendable
	interface SectionBuilder extends MetadataContainerBuilder<SectionBuilder> {
		/**
		 * Adds a value to this config file
		 *
		 * A field should be either:
		 * <ul>
		 *     <li>A basic type (int, long, float, double, boolean, String, or enum)</li>
		 *     <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types)</li>
		 * </ul>
		 * @return this
		 */
		SectionBuilder field(TrackedValue<?> value);

		/**
		 * Creates a new section nested within this one
		 */
		SectionBuilder section(String key, Consumer<SectionBuilder> creator);

		/**
		 * Create or configure a piece of metadata
		 *
		 * @param type the type of metadata to configure
		 * @param builderConsumer the modifications to be made to the piece of metadata
		 * @return this
		 */
		<M, B extends MetadataType.Builder<M>> SectionBuilder metadata(MetadataType<M, B> type, Consumer<B> builderConsumer);
	}
}
