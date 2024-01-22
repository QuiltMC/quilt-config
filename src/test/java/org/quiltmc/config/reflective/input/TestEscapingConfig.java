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

package org.quiltmc.config.reflective.input;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;

public class TestEscapingConfig extends ReflectiveConfig {
	@SerializedName("gaming.awesome")
	public final TrackedValue<String> awesome = this.value("gaming@1");

	@SerializedName("servers.server.served")
	public final TrackedValue<ValueMap<String>> servers = this.value(
		ValueMap.builder("").put("s@dir/otherdir/thirddir", "rai minecraft").put("m@www.raiminecraft.com", "minecraft").build());

	@SerializedName("cool.awesome@list[]neat")
	public final TrackedValue<ValueList<String>> awesomeList = this.value(
		ValueList.create("", "gkjasdkjfghsa^&%^&...gsd"));
}
