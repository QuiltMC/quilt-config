/*
 * Copyright 2026 QuiltMC
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestReflectiveSectionMetadata extends AbstractConfigTest {
	@Test
	void testSectionMetadataOnValue() {
		TestConfigOnValue config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "testConfig", TestConfigOnValue.class);
		NamingScheme expectedScheme = NamingSchemes.SNAKE_CASE;

		// section field
		Assertions.assertTrue(config.nested1.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme sectionMetadata = config.nested1.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, sectionMetadata);

		// field inside section
		Assertions.assertTrue(config.nested1.sectionValue.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme valueMetadata = config.nested1.sectionValue.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, valueMetadata);

		Assertions.assertTrue(config.nested1.hasMetadata(Comment.TYPE));
		// values with no comments have empty comment iterators
		Assertions.assertFalse(config.nested1.sectionValue.metadata(Comment.TYPE).iterator().hasNext());
	}

	@Test
	void testSectionMetadataOnDoubleNestedValue() {
		TestConfigOnDoubleNestedValue config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "testConfig", TestConfigOnDoubleNestedValue.class);
		NamingScheme expectedScheme = NamingSchemes.SNAKE_CASE;

		// section field
		Assertions.assertTrue(config.nested1.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme sectionMetadata = config.nested1.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, sectionMetadata);

		// section inside section
		Assertions.assertTrue(config.nested1.nested2.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme nestedMetadata = config.nested1.nested2.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, nestedMetadata);

		// field inside section
		Assertions.assertTrue(config.nested1.nested2.sectionValue.hasMetadata(SerializedNameConvention.TYPE));
		NamingScheme valueMetadata = config.nested1.nested2.sectionValue.metadata(SerializedNameConvention.TYPE);
		Assertions.assertEquals(expectedScheme, valueMetadata);

		Assertions.assertTrue(config.nested1.hasMetadata(Comment.TYPE));
		// values with no comments have empty comment iterators
		Assertions.assertFalse(config.nested1.nested2.metadata(Comment.TYPE).iterator().hasNext());
		Assertions.assertFalse(config.nested1.nested2.sectionValue.metadata(Comment.TYPE).iterator().hasNext());
	}

	@Test
	void testSectionMetadataOnClass() {
		// redirect console output so we can check for a warning
		PrintStream sysOut = System.out;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		TestConfigOnClass config = ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "testConfig", TestConfigOnClass.class);

		Assertions.assertTrue(outputStream.toString().contains("(Quilt Config) [WARNING] Annotation"));

		Assertions.assertTrue(config.nested1.hasMetadata(Comment.TYPE));
		// values with no comments have empty comment iterators
		Assertions.assertFalse(config.nested1.sectionValue.metadata(Comment.TYPE).iterator().hasNext());

		outputStream.reset();
		ConfigFactory.create(TestUtil.TOML_ENV, "testmod", "testConfig2", TestArbitraryAnnotationOnClass.class);
		Assertions.assertFalse(outputStream.toString().contains("(Quilt Config) [WARNING] Annotation"));

		// restore normal console output
		System.setOut(sysOut);
	}

	public static class TestConfigOnValue extends ReflectiveConfig {
		@Comment("Section!")
		@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
		public final Nested nested1 = new Nested();

		public static final class Nested extends ReflectiveConfig.Section {
			public final TrackedValue<Integer> sectionValue = this.value(0);
		}
	}

	public static class TestConfigOnDoubleNestedValue extends ReflectiveConfig {
		@Comment("Section!")
		@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
		public final Nested1 nested1 = new Nested1();

		public static final class Nested1 extends ReflectiveConfig.Section {
			public final Nested2 nested2 = new Nested2();

			public static final class Nested2 extends ReflectiveConfig.Section {
				public final TrackedValue<Integer> sectionValue = this.value(0);
			}
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

	@SuppressWarnings("all")
	public static class TestArbitraryAnnotationOnClass extends ReflectiveConfig {
		@Comment("Section!")
		public final Nested nested1 = new Nested();

		@Deprecated(forRemoval = true)
		public static final class Nested extends ReflectiveConfig.Section {
			public final TrackedValue<Integer> sectionValue = this.value(0);
		}
	}
}
