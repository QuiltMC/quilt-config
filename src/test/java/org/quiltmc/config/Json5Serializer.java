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

import org.quiltmc.config.impl.ConfigImpl;
import org.quiltmc.json5.JsonReader;
import org.quiltmc.json5.JsonToken;
import org.quiltmc.json5.JsonWriter;
import org.quiltmc.config.api.*;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.exceptions.ConfigParseException;
import org.quiltmc.config.api.values.*;
import org.quiltmc.config.impl.tree.TrackedValueImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Json5Serializer implements Serializer {
	public static final Json5Serializer INSTANCE = new Json5Serializer();

	private Json5Serializer() {

	}

	@Override
	public String getFileExtension() {
		return "json5";
	}

	private void serialize(JsonWriter writer, Object value, Map<Class<?>, ConfigTypeWrapper<?, ?>> configTypeWrapper) throws IOException {
		if (value instanceof Integer) {
			writer.value((Integer) value);
		} else if (value instanceof Long) {
			writer.value((Long) value);
		} else if (value instanceof Float) {
			writer.value((Float) value);
		} else if (value instanceof Double) {
			writer.value((Double) value);
		} else if (value instanceof Boolean) {
			writer.value((Boolean) value);
		} else if (value instanceof String) {
			writer.value((String) value);
		} else if (value instanceof ValueList<?>) {
			writer.beginArray();

			for (Object v : (ValueList<?>) value) {
				serialize(writer, v, configTypeWrapper);
			}

			writer.endArray();
		} else if (value instanceof ValueMap<?>) {
			writer.beginObject();

			for (Map.Entry<String, ?> entry : (ValueMap<?>) value) {
				writer.name(entry.getKey());
				serialize(writer, entry.getValue(), configTypeWrapper);
			}

			writer.endObject();
		} else if (value instanceof ConfigSerializableObject) {
			serialize(writer, ((ConfigSerializableObject<?>) value).getRepresentation(), configTypeWrapper);
		} else if (value == null) {
			writer.nullValue();
		} else if (value.getClass().isEnum()) {
			writer.value(((Enum<?>) value).name());
		} else if (configTypeWrapper.containsKey(value.getClass())) {
			ConfigTypeWrapper wrapper = configTypeWrapper.get(value.getClass());
			serialize(writer, wrapper.getRepresentation(value), configTypeWrapper);
		} else {
			throw new ConfigParseException();
		}
	}

	private void serialize(JsonWriter writer, ValueTreeNode node, Map<Class<?>, ConfigTypeWrapper<?, ?>> configTypeWrapper) throws IOException {
		for (String comment : node.metadata(Comment.TYPE)) {
			writer.comment(comment);
		}

		if (node instanceof ValueTreeNode.Section) {
			writer.name(node.key().getLastComponent());
			writer.beginObject();

			for (ValueTreeNode child : ((ValueTreeNode.Section) node)) {
				serialize(writer, child, configTypeWrapper);
			}

			writer.endObject();
		} else {
			TrackedValue<?> trackedValue = ((TrackedValue<?>) node);
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

				writer.comment(options.toString());
			}

			for (Constraint<?> constraint : trackedValue.constraints()) {
				writer.comment(constraint.getRepresentation());
			}

			if (!(defaultValue instanceof CompoundConfigValue<?>)) {
				writer.comment("default: " + defaultValue);
			}

			writer.name(node.key().getLastComponent());

			serialize(writer, trackedValue.getRealValue(), configTypeWrapper);
		}
	}

	@Override
	public void serialize(Config config, OutputStream to) throws IOException {
		JsonWriter writer = JsonWriter.json5(new OutputStreamWriter(to));

		for (String comment : config.metadata(Comment.TYPE)) {
			writer.comment(comment);
		}

		writer.beginObject();

		for (ValueTreeNode node : config.nodes()) {
			this.serialize(writer, node, ((ConfigImpl)config).getTypeWrapper());
		}

		writer.endObject();
		writer.close();
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void deserialize(Config config, InputStream from) {
		try {
			JsonReader reader = JsonReader.json5(new InputStreamReader(from));

			Map<String, Object> values = parseObject(reader);

			for (TrackedValue<?> value : config.values()) {
				Map<String, Object> m = values;

				for (int i = 0; i < value.key().length(); ++i) {
					String k = value.key().getKeyComponent(i);

					if (m.containsKey(k) && i != value.key().length() - 1) {
						m = (Map<String, Object>) m.get(k);
					} else if (m.containsKey(k)) {
						((TrackedValueImpl) value).setValue(MarshallingUtils.coerce(m.get(k), value.getDefaultValue(), (Map<String, ?> map, MarshallingUtils.MapEntryConsumer entryConsumer) ->
								map.forEach(entryConsumer::put), ((ConfigImpl)config).getTypeWrapper()), false);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, Object> parseObject(JsonReader reader) throws IOException {
		reader.beginObject();

		Map<String, Object> object = new LinkedHashMap<>();

		while (reader.hasNext() && reader.peek() == JsonToken.NAME) {
			object.put(reader.nextName(), parseElement(reader));
		}

		reader.endObject();

		return object;
	}

	public static List<Object> parseArray(JsonReader reader) throws IOException {
		reader.beginArray();

		List<Object> array = new ArrayList<>();

		while (reader.hasNext() && reader.peek() != JsonToken.END_ARRAY) {
			array.add(parseElement(reader));
		}

		reader.endArray();

		return array;
	}

	private static Object parseElement(JsonReader reader) throws IOException {
		switch (reader.peek()) {
			case END_ARRAY:
				throw new ConfigParseException("Unexpected end of array");
			case BEGIN_OBJECT:
				return parseObject(reader);
			case BEGIN_ARRAY:
				return parseArray(reader);
			case END_OBJECT:
				throw new ConfigParseException("Unexpected end of object");
			case NAME:
				throw new ConfigParseException("Unexpected name");
			case STRING:
				return reader.nextString();
			case NUMBER:
				return reader.nextNumber();
			case BOOLEAN:
				return reader.nextBoolean();
			case NULL:
				reader.nextNull();
				return null;
			case END_DOCUMENT:
				throw new ConfigParseException("Unexpected end of file");
		}

		throw new ConfigParseException("Encountered unknown JSON token");
	}
}
