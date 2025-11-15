/*
 * Copyright 2024 QuiltMC
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
import org.quiltmc.config.api.Serializer;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * A default serializer that writes in the <a href="https://code.visualstudio.com/docs/languages/json#_json-with-comments">JSON with Comments format</a>.
 */
public final class JsonCSerializer extends AbstractJsonSerializer implements Serializer {
	public static final JsonCSerializer INSTANCE = new JsonCSerializer();

	private JsonCSerializer() {}

	@Override
	public String getFileExtension() {
		return "jsonc";
	}

	@Override
	public void serialize(Config config, OutputStream to) throws IOException {
		this.serialize(config, JsonWriter.jsonc(new OutputStreamWriter(to)));
	}

	@Override
	public void deserialize(Config config, InputStream from) {
		this.deserialize(config, JsonReader.create(new InputStreamReader(from), JsonFormat.JSONC));
	}
}
