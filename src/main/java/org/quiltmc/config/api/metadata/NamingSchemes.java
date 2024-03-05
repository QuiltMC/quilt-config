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

package org.quiltmc.config.api.metadata;

import java.util.ArrayList;


public enum NamingSchemes implements NamingScheme {
	/**
	 * Does not format the string at all. {@code "funnyStringAsAnExample"} becomes {@code "funnyStringAsAnExample"}
	 */
	PASSTHROUGH {
		@Override
		public String coerce(String input) {
			return input;
		}
	},
	/**
	 * Formats the string as {@code UpperCamelCase}. {@code "funny string as an example"} becomes {@code "FunnyStringAsAnExample"}
	 */
	UPPER_CAMEL_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					builder.appendCodePoint(Character.toUpperCase(word.codePointAt(0)));
					builder.append(word.substring(1));
				}
			}
			return builder.toString();
		}
	},
	/**
	 * Formats the string as {@code lowerCamelCase}. {@code "funny string as an example"} becomes {@code "funnyStringAsAnExample"}
	 */
	LOWER_CAMEL_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						builder.append(word);
						first = false;
					} else {
						builder.appendCodePoint(Character.toUpperCase(word.codePointAt(0)));
						builder.append(word.substring(1));
					}
				}
			}
			return builder.toString();
		}
	},
	/**
	 * Formats the string as {@code kebab-case}. {@code "funny string as an example"} becomes {@code "funny-string-as-an-example"}
	 */
	KEBAB_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						first = false;
					} else {
						builder.append("-");
					}
					builder.append(word);
				}
			}
			return builder.toString();
		}
	},
	/**
	 * Formats the string as {@code snake_case}. {@code "funny string as an example"} becomes {@code "funny_string_as_an_example"}
	 */
	SNAKE_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						first = false;
					} else {
						builder.append("_");
					}
					builder.append(word);
				}
			}
			return builder.toString();
		}
	},
	/**
	 * Formats the string as space separated lower case. {@code "funnyStringAsAnExample"} becomes {@code "funny string as an example"}
	 */
	SPACE_SEPARATED_LOWER_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						first = false;
					} else {
						builder.append(" ");
					}
					builder.append(word);
				}
			}
			return builder.toString();
		}
	},
	/**
	 * Formats the string as space separated lower case with the first word being capitalized. {@code "funnyStringAsAnExample"} becomes {@code "Funny string as an example"}
	 */
	SPACE_SEPARATED_LOWER_CASE_INITIAL_UPPER_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						builder.appendCodePoint(Character.toUpperCase(word.codePointAt(0)));
						builder.append(word.substring(1));
						first = false;
					} else {
						builder.append(" ");
						builder.append(word);
					}
				}
			}
			return builder.toString();
		}

	},
	/**
	 * Formats the string as {@code Title Case}, all words are capitalized. {@code "funnyStringAsAnExample"} becomes {@code "Funny String As An Example"}
	 */
	TITLE_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						first = false;
					} else {
						builder.append(" ");
					}
					builder.appendCodePoint(Character.toUpperCase(word.codePointAt(0)));
					builder.append(word.substring(1));
				}
			}
			return builder.toString();
		}
	};

	private static String[] extractWords(String input) {
		ArrayList<String> list = new ArrayList<>();
		final StringBuilder[] builder = {new StringBuilder()};
		// 0: last was word boundary, 1: last was upper case, 2: last was neither boundary nor upper case
		final int[] state = {0};
		input.codePoints().forEach(point -> {
			if (point == '-' || point == '_' || point == ' ') {
				list.add(builder[0].toString());
				builder[0] = new StringBuilder();
				state[0] = 0;
			} else {
				boolean isUpper = Character.isUpperCase(point);
				if (isUpper) {
					if (state[0] == 2) {
						list.add(builder[0].toString());
						builder[0] = new StringBuilder();
						state[0] = 0;
					} else {
						state[0] = 1;
					}
				} else {
					state[0] = 2;
				}
				builder[0].appendCodePoint(Character.toLowerCase(point));
			}
		});
		list.add(builder[0].toString());
		return list.toArray(new String[0]);
	}
}
