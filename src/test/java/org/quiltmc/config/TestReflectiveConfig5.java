package org.quiltmc.config;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.NameConvention;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.naming.NamingSchemes;

@NameConvention(NamingSchemes.SNAKE_CASE)
public final class TestReflectiveConfig5 extends WrappedConfig {
    public final String sampleText = "haha yes";

    @NameConvention(NamingSchemes.LOWER_CASE_DASHED)
    public final boolean differentConvention = true;

    @SerializedName("a_completely_different_name")
    public final int someName = 24;

    public final Nested differentConventionForSection = new Nested();

    @NameConvention(NamingSchemes.CAMEL_CASE)
    public final Nested overridenConventionForSection = new Nested();

    @NameConvention(custom = "org.quiltmc.config.ScreamingSnakeCaseNamingScheme")
    public static final class Nested implements Config.Section {
        public final String hello;
        public final boolean customConvention;

        public Nested() {
            this.hello = "world";
            this.customConvention = true;
        }
    }
}
