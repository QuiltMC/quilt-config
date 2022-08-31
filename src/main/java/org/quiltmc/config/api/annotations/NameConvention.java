/*
 * Copyright 2022 QuiltMC
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
package org.quiltmc.config.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.naming.NamingScheme;
import org.quiltmc.config.api.naming.NamingSchemes;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface NameConvention {
    MetadataType<NamingScheme, Builder> TYPE = MetadataType.create(() -> Optional.of(NamingSchemes.PASSTHROUGH), Builder::new);

    NamingSchemes value() default NamingSchemes.PASSTHROUGH;
    String custom() default "";

    final class Builder implements MetadataType.Builder<NamingScheme> {
        private NamingScheme scheme;

        public Builder() {
            scheme = NamingSchemes.PASSTHROUGH;
        }

        public void set(NamingScheme scheme) {
            this.scheme = scheme;
        }

        @Override
        public NamingScheme build() {
            return scheme;
        }
    }
}
