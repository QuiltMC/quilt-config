package org.quiltmc.config.reflective;

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
