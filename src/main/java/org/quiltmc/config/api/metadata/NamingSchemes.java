package org.quiltmc.config.api.metadata;

import java.util.ArrayList;

public enum NamingSchemes implements NamingScheme {
	PASSTHROUGH {
		@Override
		public String coerce(String input) {
			return input;
		}
	},
	UPPER_CAMEL_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					builder.append(Character.toUpperCase(word.codePointAt(0)));
					builder.append(word.substring(1));
				}
			}
			return builder.toString();
		}
	},
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
						builder.append(Character.toUpperCase(word.codePointAt(0)));
						builder.append(word.substring(1));
					}
				}
			}
			return builder.toString();
		}
	},
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
	SPACE_SEPARATED_LOWER_CASE_INITIAL_UPPER_CASE {
		@Override
		public String coerce(String input) {
			final StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String word : NamingSchemes.extractWords(input)) {
				if (!word.isEmpty()) {
					if (first) {
						builder.append(Character.toUpperCase(word.codePointAt(0)));
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
					builder.append(Character.toUpperCase(word.codePointAt(0)));
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
