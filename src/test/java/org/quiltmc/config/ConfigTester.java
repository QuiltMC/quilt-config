/*
 * Copyright 2022-2023 QuiltMC
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ConfigEnvironment;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.exceptions.ConfigCreationException;
import org.quiltmc.config.api.exceptions.ConfigFieldException;
import org.quiltmc.config.api.exceptions.TrackedValueException;
import org.quiltmc.config.api.metadata.Comments;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.serializer.Json5Serializer;
import org.quiltmc.config.api.serializer.TomlSerializer;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.impl.CommentsImpl;
import org.quiltmc.config.implementor_api.ConfigFactory;
import org.quiltmc.config.reflective.TestValueConfig3;
import org.quiltmc.config.reflective.TestValueConfig4;
import org.quiltmc.config.reflective.TestValueListConfig;
import org.quiltmc.config.reflective.TestValueMapConfig;
import org.quiltmc.config.reflective.TestReflectiveConfig;
import org.quiltmc.config.reflective.TestReflectiveConfig2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class ConfigTester {
	static Path TEMP = Paths.get("temp");
	static ConfigEnvironment ENV;

	static TrackedValue<String> TEST;
	static TrackedValue<Integer> TEST_INTEGER;
	static TrackedValue<Boolean> TEST_BOOLEAN;
	static TrackedValue<String> TEST_STRING;
	static TrackedValue<ValueList<Integer>> TEST_LIST;

	@BeforeAll
	public static void initializeConfigDir() {
		ENV = new ConfigEnvironment(TEMP, TomlSerializer.INSTANCE, Json5Serializer.INSTANCE);
	}

	@Test
	public void testSerializer() {
		Config config = Config.create(ENV, "testmod", "testConfig6", builder -> {
			builder.field(TrackedValue.create(0, "testInteger", creator -> {
				creator.metadata(Comment.TYPE, comments -> comments.add("Comment one"));
				creator.metadata(Comment.TYPE, comments -> comments.add("Comment two"));
				creator.metadata(Comment.TYPE, comments -> comments.add("Comment three"));
			}));
			builder.section("super_awesome_section", section1 -> {
				section1.metadata(Comment.TYPE, comments -> comments.add("This is a section comment!"));
				section1.metadata(SerializedName.TYPE, name -> name.withName("super_duper_awesome_section"));
				section1.field(TrackedValue.create(1, "before"));
				section1.section("less_awesome_section", section2 -> {
					section2.metadata(SerializedName.TYPE, name -> name.withName("much_less_awesome_section"));
					section2.metadata(Comment.TYPE, comments -> comments.add("This is another section comment!"));
					section2.section("regular_section", section3 -> {
						section3.field(TrackedValue.create(0, "water"));
						section3.field(TrackedValue.create(0, "earth"));
						section3.field(TrackedValue.create(0, "fire", creator -> {
							creator.metadata(Comment.TYPE, comments -> comments.add("This is a field comment!"));
							creator.metadata(Comment.TYPE, comments -> comments.add("This is another field comment!"));

						}));
						section3.field(TrackedValue.create(0, "air"));
					});
					section2.field(TrackedValue.create("lemonade", "crunchy_ice"));
				});
				section1.field(TEST = TrackedValue.create("woot", "after"));
			});
			builder.field(TrackedValue.create(true, "testtt32"));
			builder.field(TrackedValue.create(false, "testBoolean"));
			builder.field(TrackedValue.create("blah", "testString"));
			builder.field(TrackedValue.create(100, "a", "b", "c1", "d"));
			builder.field(TrackedValue.create(1234, "a", "b", "c2"));
			builder.field(TrackedValue.create(
					ValueList.create(0, 1, 2, 3, 4), "testList1"
			));
			builder.field(TrackedValue.create(
					ValueList.create(ValueMap.builder(ValueList.create("")).put("one", ValueList.create("")).build(),
							ValueMap.builder(ValueList.create("")).put("one", ValueList.create("")).build(),
							ValueMap.builder(ValueList.create("")).put("one", ValueList.create("")).build(),
							ValueMap.builder(ValueList.create("")).put("one", ValueList.create("")).build()), "testList2"
			));
		});

		TEST.setValue("This value was set programmatically!", true);
	}

	@Test
	public void testValidation() {
		Assertions.assertThrows(TrackedValueException.class, () -> Config.create(ENV, "testmod", "testConfig1", builder -> builder.field(TrackedValue.create(new ArrayList<Integer>(), "boop"))));

		Assertions.assertThrows(TrackedValueException.class, () -> Config.create(ENV, "testmod", "testConfig2", builder -> builder.field(TrackedValue.create(ValueList.create(new ArrayList<Integer>()), "boop"))));
	}

	@Test
	public void testValues() {
		Config config = Config.create(ENV, "testmod", "testConfig3", builder -> {
			builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger"));
			builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
			builder.field(TEST_STRING  = TrackedValue.create("blah", "testString"));
			builder.field(TEST_LIST = TrackedValue.create(
					ValueList.create(0, 1, 2, 3, 4), "testList"
			));
			builder.section("testSection", section -> {
				section.metadata(Comment.TYPE, comments -> comments.add("Section comment 1"));
				section.metadata(Comment.TYPE, comments -> comments.add("Section comment 2"));
				section.metadata(Comment.TYPE, comments -> comments.add("Section comment 3"));
				section.field(TrackedValue.create("wooooh", "emote"));
				section.field(TrackedValue.create("etrator", "perp"));
			});
			builder.callback(c -> System.out.println("We was updated!"));
		});

		TEST_STRING.registerCallback((value) ->
				System.out.printf("Value '%s' updated: New value: '%s'%n", value.key(), value.value())
		);

		for (TrackedValue<?> value : config.values()) {
			System.out.printf("\"%s\": %s%n", value.key(), value.value());
		}

		TEST_STRING.setValue("walalala", true);

		System.out.println();

		for (TrackedValue<?> value : config.values()) {
			System.out.printf("\"%s\": %s%n", value.key(), value.value());
		}
	}

	@Test
	public void testMetadata() {
		Config config = Config.create(ENV, "testmod", "testConfig4", builder -> {
			builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger", creator -> creator.metadata(Comment.TYPE, comments -> comments.add(
					"Comment one",
					"Comment two",
					"Comment three"
			))));
			builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
			builder.field(TEST_STRING  = TrackedValue.create("blah", "testString"));
		});

		for (TrackedValue<?> value : config.values()) {
			System.out.printf("\"%s\": %s%n", value.key(), value.value());

			for (String comment : value.metadata(Comment.TYPE)) {
				System.out.printf("\t// %s%n", comment);
			}
		}
	}

	@Test
	public void testFlags() {
		Config config = Config.create(ENV, "testmod", "testConfig5", builder -> {
			builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger"));
			builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
			builder.field(TEST_STRING  = TrackedValue.create("blah", "testString"));
		});

		for (TrackedValue<?> value : config.values()) {
			System.out.printf("\"%s\": %s%n", value.key(), value.value());

			for (String comment : value.metadata(Comment.TYPE)) {
				System.out.printf("\t// %s%n", comment);
			}
		}
	}

	@Test
	public void testConstraints() {
		Assertions.assertThrows(TrackedValueException.class, () -> Config.create(ENV, "testmod", "testConfig6", builder -> {
			builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger", creator -> {
				// Should throw an exception since the default value is outside of the constraint range
				creator.constraint(Constraint.range(5, 10));
			}));
			builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
			builder.field(TEST_STRING  = TrackedValue.create("blah", "testString"));
		}));

		Assertions.assertThrows(TrackedValueException.class, () -> {
			Config.create(ENV, "testmod", "testConfig7", builder -> {
				builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger", creator -> creator.constraint(Constraint.range(-10, 10))));
				builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
				builder.field(TEST_STRING  = TrackedValue.create("blah", "testString"));
			});

			TEST_INTEGER.setValue(1000, true);
		});

		Assertions.assertThrows(TrackedValueException.class, () -> {
			Config.create(ENV, "testmod", "testConfig8", builder -> {
				builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger", creator -> creator.constraint(Constraint.range(-10, 10))));
				builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
				builder.field(TEST_STRING  = TrackedValue.create("blah", "test", creator -> creator.constraint(Constraint.matching("[a-zA-Z0-9]+:[a-zA-Z0-9]+"))));
			});

			TEST_INTEGER.setValue(1000, true);
		});

		Config.create(ENV, "testmod", "testConfig9", builder -> {
			builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger", creator -> creator.constraint(Constraint.range(-10, 10))));
			builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
			builder.field(TEST_STRING  = TrackedValue.create("test:id", "test", creator -> creator.constraint(Constraint.matching("[a-zA-Z0-9]+:[a-zA-Z0-9]+"))));
		});
	}

	public void testWrappedConfigs(String id, String format) {
		TestReflectiveConfig config = ConfigFactory.create(ENV, "testmod", id, TestReflectiveConfig.class, builder -> builder.format(format));

		for (TrackedValue<?> value : config.values()) {
			System.out.printf("\"%s\": %s%n", value.key(), value.value());

			for (String comment : value.metadata(Comment.TYPE)) {
				System.out.printf("\t// %s%n", comment);
			}
		}

		Assertions.assertThrows(ConfigCreationException.class, () -> ConfigFactory.create(ENV, "testmod", "testConfig", TestReflectiveConfig2.class)).printStackTrace();

		Assertions.assertThrows(ConfigFieldException.class, () -> ConfigFactory.create(ENV, "testmod", "testConfig", TestValueConfig3.class)).printStackTrace();

		Assertions.assertThrows(TrackedValueException.class, () -> ConfigFactory.create(ENV, "testmod", "testConfig", TestValueConfig4.class)).printStackTrace();
	}

	@Test
	public void testWrappedConfigs() {
		testWrappedConfigs("testConfig10", "toml");
		testWrappedConfigs("testConfig11", "json5");
	}

	@Test
	public void testTomlConfigs() {
		TestReflectiveConfig config = ConfigFactory.create(ENV, "testmod", "testConfig12", TestReflectiveConfig.class, builder -> builder.format("toml"));

		for (TrackedValue<?> value : config.values()) {
			System.out.printf("\"%s\": %s%n", value.key(), value.value());

			for (String comment : value.metadata(Comment.TYPE)) {
				System.out.printf("\t// %s%n", comment);
			}
		}
	}

	@Test
	public void testValueMapBehavior() {
		TestValueMapConfig c = ConfigFactory.create(ENV, "testmod", "testConfig13", TestValueMapConfig.class);

		c.weights.value().put("" + c.weights.value().size(), c.weights.value().size());
	}

	@Test
	public void testValueListBehavior() {
		TestValueListConfig c = ConfigFactory.create(ENV, "testmod", "testConfig14", TestValueListConfig.class);

		c.strings.value().add(c.strings.value().size() + "");
	}

	@Test
	public void testInferredMetadataType() {
		MetadataType<Comments, Comment.Builder> TYPE = MetadataType.create(() -> Optional.of(new CommentsImpl(Collections.emptyList())), type -> {
			if (type == String.class) {
				return Optional.of(new CommentsImpl(Collections.singletonList("marinara")));
			} else {
				return Optional.of(new CommentsImpl(Collections.emptyList()));
			}
		}, Comment.Builder::new);

		Config config = Config.create(ENV, "testmod", "testConfig400", builder -> {
			builder.field(TEST_INTEGER = TrackedValue.create(0, "testInteger", creator -> creator.metadata(TYPE, comments -> comments.add(
					"Comment one",
					"Comment two",
					"Comment three"
			))));
			builder.field(TEST_BOOLEAN = TrackedValue.create(false, "testBoolean"));
			builder.field(TEST_STRING  = TrackedValue.create("blah", "testString"));
		});

		for (TrackedValue<?> value : config.values()) {
			for (String comment : value.metadata(TYPE)) {
				System.out.printf("%s: %s%n", value.key(), comment);
			}
		}
	}
}
