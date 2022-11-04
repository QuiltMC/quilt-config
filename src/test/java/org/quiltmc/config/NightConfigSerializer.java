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
package org.quiltmc.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ConfigTypeWrapper;
import org.quiltmc.config.api.MarshallingUtils;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.*;
import org.quiltmc.config.impl.ConfigImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NightConfigSerializer<C extends CommentedConfig> implements Serializer {
	private final String fileExtension;
	private final ConfigParser<C> parser;
	private final ConfigWriter writer;

	public NightConfigSerializer(String fileExtension, ConfigParser<C> parser, ConfigWriter writer) {
		this.fileExtension = fileExtension;
		this.parser = parser;
		this.writer = writer;
	}

	@Override
	public String getFileExtension() {
		return this.fileExtension;
	}

	@Override
	public void serialize(Config config, OutputStream to) {
		this.writer.write(write(createCommentedConfig(), config.nodes(), ((ConfigImpl)config).getTypeWrapper()), to);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void deserialize(Config config, InputStream from) {
		CommentedConfig read = this.parser.parse(from);

		for (TrackedValue<?> trackedValue : config.values()) {
			if (read.contains(trackedValue.key().toString())) {
				((TrackedValue) trackedValue).setValue(MarshallingUtils.coerce(read.get(trackedValue.key().toString()), trackedValue.getDefaultValue(), (CommentedConfig c, MarshallingUtils.MapEntryConsumer entryConsumer) ->
						c.entrySet().forEach(e -> entryConsumer.put(e.getKey(), e.getValue())), ((ConfigImpl)config).getTypeWrapper()), false);
			}
		}
	}

	private static List<Object> convertList(List<?> list, Map<Class<?>, ConfigTypeWrapper<?, ?>> configTypeWrapper) {
		List<Object> result = new ArrayList<>(list.size());

		for (Object value : list) {
			result.add(convertAny(value, configTypeWrapper));
		}

		return result;
	}

	private static UnmodifiableCommentedConfig convertMap(ValueMap<?> map, Map<Class<?>, ConfigTypeWrapper<?, ?>> configTypeWrapper) {
		CommentedConfig result = createCommentedConfig();

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			result.add(entry.getKey(), convertAny(entry.getValue(), configTypeWrapper));
		}

		return result;
	}

	private static Object convertAny(Object value, Map<Class<?>, ConfigTypeWrapper<?, ?>> configTypeWrapper) {
		if (value instanceof ValueMap) {
			return convertMap((ValueMap<?>) value, configTypeWrapper);
		} else if (value instanceof ValueList) {
			return convertList((ValueList<?>) value, configTypeWrapper);
		} else if (value instanceof ConfigSerializableObject) {
			return convertAny(((ConfigSerializableObject<?>) value).getRepresentation(), configTypeWrapper);
		} else if (configTypeWrapper.containsKey(value.getClass())) {
			ConfigTypeWrapper wrapper = configTypeWrapper.get(value.getClass());
			return convertAny(wrapper.getRepresentation(value), configTypeWrapper);
		} else {
			return value;
		}
	}

	private static CommentedConfig write(CommentedConfig config, Iterable<ValueTreeNode> nodes, Map<Class<?>, ConfigTypeWrapper<?, ?>> configTypeWrapper) {
		for (ValueTreeNode node : nodes) {
			List<String> comments = new ArrayList<>();

			if (node.hasMetadata(Comment.TYPE)) {
				for (String string : node.metadata(Comment.TYPE)) {
					comments.add(string);
				}
			}

			if (node instanceof TrackedValue) {
				TrackedValue<?> trackedValue = (TrackedValue<?>) node;
				Object defaultValue = trackedValue.getDefaultValue();

				if (defaultValue.getClass().isEnum()) {
					StringBuilder options = new StringBuilder("options: ");
					Object[] enumConstants = defaultValue.getClass().getEnumConstants();

					for (int i = 0, enumConstantsLength = enumConstants.length; i < enumConstantsLength; i++) {
						Object o = enumConstants[i];

						options.append(o);

						if (i < enumConstantsLength - 1) {
							options.append(", ");
						}
					}

					comments.add(options.toString());
				}

				if (!(defaultValue instanceof CompoundConfigValue<?>)) {
					comments.add("default: " + defaultValue);
				}
				config.add(trackedValue.key().toString(), convertAny(trackedValue.getRealValue(), configTypeWrapper));
			} else {
				write(config, ((ValueTreeNode.Section) node), configTypeWrapper);
			}

			if (!comments.isEmpty()) {
				config.setComment(node.key().toString(), " " + String.join("\n ", comments));
			}
		}

		return config;
	}

	private static CommentedConfig createCommentedConfig() {
		return InMemoryCommentedFormat.defaultInstance().createConfig(LinkedHashMap::new);
	}
}
