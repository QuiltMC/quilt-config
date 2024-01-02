/*
 * Copyright 2023-2024 QuiltMC
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

package org.quiltmc.config.api.serializer;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.MarshallingUtils;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.CompoundConfigValue;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.SerializerUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A default serializer that writes in the <a href="https://toml.io/en/">TOML format</a>.
 */
public final class TomlSerializer implements Serializer {
	public static final TomlSerializer INSTANCE = new TomlSerializer();
	private final ConfigParser<CommentedConfig> parser = new TomlParser();
	private final ConfigWriter writer = new TomlWriter();

	private TomlSerializer() {

	}

	@Override
	public String getFileExtension() {
		return "toml";
	}

	@Override
	public void serialize(Config config, OutputStream to) {
		this.writer.write(write(config, createCommentedConfig(), config.nodes()), to);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void deserialize(Config config, InputStream from) {
		CommentedConfig read = this.parser.parse(from);

		for (TrackedValue<?> trackedValue : config.values()) {
			String key = SerializerUtils.getSerializedKey(config, trackedValue);

			if (read.contains(key)) {
				((TrackedValue) trackedValue).setValue(MarshallingUtils.coerce(read.get(key), trackedValue.getDefaultValue(), (CommentedConfig c, MarshallingUtils.MapEntryConsumer entryConsumer) ->
					c.entrySet().forEach(e -> entryConsumer.put(e.getKey(), e.getValue()))), false);
			}
		}
	}

	private static List<Object> convertList(List<?> list) {
		List<Object> result = new ArrayList<>(list.size());

		for (Object value : list) {
			result.add(convertAny(value));
		}

		return result;
	}

	private static UnmodifiableCommentedConfig convertMap(ValueMap<?> map) {
		CommentedConfig result = createCommentedConfig();

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			result.add(entry.getKey(), convertAny(entry.getValue()));
		}

		return result;
	}

	private static Object convertAny(Object value) {
		if (value instanceof ValueMap) {
			return convertMap((ValueMap<?>) value);
		} else if (value instanceof ValueList) {
			return convertList((ValueList<?>) value);
		} else if (value instanceof ConfigSerializableObject) {
			return convertAny(((ConfigSerializableObject<?>) value).getRepresentation());
		} else {
			return value;
		}
	}

	private static CommentedConfig write(Config config, CommentedConfig commentedConfig, Iterable<ValueTreeNode> nodes) {
		for (ValueTreeNode node : nodes) {
			List<String> comments = new ArrayList<>();

			if (node.hasMetadata(Comment.TYPE)) {
				for (String string : node.metadata(Comment.TYPE)) {
					comments.add(string);
				}
			}

			String key = SerializerUtils.getSerializedKey(config, node);
			//key = node.key().toString();

			if (node instanceof TrackedValue<?>) {
				TrackedValue<?> value = (TrackedValue<?>) node;
				Object defaultValue = value.getDefaultValue();

				SerializerUtils.createEnumOptionsComment(defaultValue).ifPresent(comments::add);

				for (Constraint<?> constraint : value.constraints()) {
					comments.add(constraint.getRepresentation());
				}

				if (!(defaultValue instanceof CompoundConfigValue<?>)) {
					comments.add("default: " + defaultValue);
				}

				commentedConfig.add(key, convertAny(value.getRealValue()));
			} else {
				write(config, commentedConfig, ((ValueTreeNode.Section) node));
			}

			if (!comments.isEmpty()) {
				commentedConfig.setComment(key, " " + String.join("\n ", comments));
			}
		}

		return commentedConfig;
	}

	private static CommentedConfig createCommentedConfig() {
		return InMemoryCommentedFormat.defaultInstance().createConfig(LinkedHashMap::new);
	}
}
