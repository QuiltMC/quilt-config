package org.quiltmc.config.reflective;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.TestUtil;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingScheme;
import org.quiltmc.config.api.metadata.NamingSchemes;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.implementor_api.ConfigFactory;

public class TestReflectiveSectionMetadata extends AbstractConfigTest {
	@Test
	void testSectionMetadataOnValue() {
		TestConfigOnValue config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "testConfig", TestConfigOnValue.class);
		NamingScheme expectedScheme = NamingSchemes.SNAKE_CASE;

		Assertions.assertTrue(config.nested1.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme sectionMetadata = config.nested1.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, sectionMetadata);

		Assertions.assertTrue(config.nested1.sectionValue.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme valueMetadata = config.nested1.sectionValue.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, valueMetadata);

		Assertions.assertTrue(config.nested1.hasMetadata(Comment.TYPE));
		// values with no comments have empty comment iterators
		Assertions.assertFalse(config.nested1.sectionValue.metadata(Comment.TYPE).iterator().hasNext());
	}

	@Test
	void testSectionMetadataOnClass() {
		TestConfigOnClass config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "testConfig", TestConfigOnClass.class);
		NamingScheme expectedScheme = NamingSchemes.SNAKE_CASE;

		Assertions.assertTrue(config.nested1.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme sectionMetadata = config.nested1.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, sectionMetadata);

		Assertions.assertTrue(config.nested1.sectionValue.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme valueMetadata = config.nested1.sectionValue.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, valueMetadata);

		Assertions.assertTrue(config.nested1.hasMetadata(Comment.TYPE));
		// values with no comments have empty comment iterators
		Assertions.assertFalse(config.nested1.sectionValue.metadata(Comment.TYPE).iterator().hasNext());
	}

	public static class TestConfigOnValue extends ReflectiveConfig {
		@Comment("Section!")
		@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
		public final Nested nested1 = new Nested();

		public static final class Nested extends ReflectiveConfig.Section {
			public final TrackedValue<Integer> sectionValue = this.value(0);
		}
	}

	public static class TestConfigOnClass extends ReflectiveConfig {
		@Comment("Section!")
		public final Nested nested1 = new Nested();

		@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
		public static final class Nested extends ReflectiveConfig.Section {
			public final TrackedValue<Integer> sectionValue = this.value(0);
		}
	}
}
