package org.quiltmc.config;

import java.util.Locale;

import org.quiltmc.config.api.naming.NamingScheme;
import org.quiltmc.config.api.naming.NamingSchemes;

public final class ScreamingSnakeCaseNamingScheme implements NamingScheme {
    @Override
    public String coerce(String input) {
        return NamingSchemes.SNAKE_CASE.coerce(input).toUpperCase(Locale.ROOT);
    }
}
