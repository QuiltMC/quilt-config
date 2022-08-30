package org.quiltmc.config;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.SerializedName;

public final class TestReflectiveConfig5 extends WrappedConfig {
    @SerializedName("a_completely_different_name")
    public final int someName = 24;

    @SerializedName("section")
    public final Nested worksForSectionsToo = new Nested();

    public static final class Nested implements Config.Section {
        public final String hello;

        public Nested() {
            this.hello = "world";
        }
    }
}
