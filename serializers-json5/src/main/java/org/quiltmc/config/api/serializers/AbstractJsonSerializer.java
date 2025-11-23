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

package org.quiltmc.config.api.serializers;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.MarshallingUtils;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.exceptions.ConfigParseException;
import org.quiltmc.config.api.values.*;
import org.quiltmc.config.impl.tree.TrackedValueImpl;
import org.quiltmc.config.impl.util.SerializerUtils;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonToken;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An abstract serializer that uses Quilt Parsers' JSON library in order to support JSON-like formats.
 */
public class AbstractJsonSerializer {
	private void serialize(JsonWriter writer, Object value) throws IOException {
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
		} else if (value instanceof BigInteger) {
			writer.value((BigInteger) value);
		} else if (value instanceof BigDecimal) {
			writer.value((BigDecimal) value);
		} else if (value instanceof ValueList<?>) {
			writer.beginArray();

			for (Object v : (ValueList<?>) value) {
				serialize(writer, v);
			}

			writer.endArray();
		} else if (value instanceof ValueMap<?>) {
			writer.beginObject();

			for (Map.Entry<String, ?> entry : (ValueMap<?>) value) {
				writer.name(entry.getKey());
				serialize(writer, entry.getValue());
			}

			writer.endObject();
		} else if (value instanceof ConfigSerializableObject) {
			serialize(writer, ((ConfigSerializableObject<?>) value).getRepresentation());
		} else if (value == null) {
			writer.nullValue();
		} else if (value.getClass().isEnum()) {
			writer.value(((Enum<?>) value).name());
		} else {
			throw new ConfigParseException();
		}
	}

	private void serialize(JsonWriter writer, ValueTreeNode node) throws IOException {
		for (String comment : node.metadata(Comment.TYPE)) {
			writer.comment(comment);
		}

		if (node instanceof ValueTreeNode.Section) {
			writer.name(SerializerUtils.getSerializedName(node));
			writer.beginObject();

			for (ValueTreeNode child : ((ValueTreeNode.Section) node)) {
				serialize(writer, child);
			}

			writer.endObject();
		} else {
			TrackedValue<?> trackedValue = ((TrackedValue<?>) node);
			Object defaultValue = trackedValue.getDefaultValue();

			Optional<String> enumOptionsComment = SerializerUtils.createEnumOptionsComment(defaultValue);
			if (enumOptionsComment.isPresent()) {
				writer.comment(enumOptionsComment.get());
			}

			for (Constraint<?> constraint : trackedValue.constraints()) {
				writer.comment(constraint.getRepresentation());
			}

			Optional<String> defaultComment = SerializerUtils.getDefaultValueString(defaultValue);
			if (defaultComment.isPresent()) {
				writer.comment("default: " + defaultComment.get());
			}

			String name = SerializerUtils.getSerializedName(trackedValue);
			writer.name(name);

			serialize(writer, trackedValue.getRealValue());
		}
	}

	public void serialize(Config config, JsonWriter writer) throws IOException {
		for (String comment : config.metadata(Comment.TYPE)) {
			writer.comment(comment);
		}

		writer.beginObject();

		for (ValueTreeNode node : config.nodes()) {
			this.serialize(writer, node);
		}

		writer.endObject();
		writer.close();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void deserialize(Config config, JsonReader reader) {
		try {
			Map<String, Object> values = parseObject(reader);

			for (TrackedValue<?> value : config.values()) {
				Map<String, Object> m = values;
				List<ValueKey> keyOptions = SerializerUtils.getPossibleKeys(config, value);

				for (ValueKey key : keyOptions) {
					for (int i = 0; i < key.length(); i++) {
						String name = key.getKeyComponent(i);
						if (m.containsKey(name) && i != key.length() - 1) {
							m = (Map<String, Object>) m.get(name);
						} else if (m.containsKey(name)) {
							((TrackedValueImpl) value).setValue(MarshallingUtils.coerce(m.get(name), value.getDefaultValue(), (Map<String, ?> map, MarshallingUtils.MapEntryConsumer entryConsumer) ->
								map.forEach(entryConsumer::put)), false);
						}
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
