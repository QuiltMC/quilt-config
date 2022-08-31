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
package org.quiltmc.config.impl.util;

import java.util.function.BiFunction;

import org.quiltmc.config.api.annotations.NameConvention;
import org.quiltmc.config.api.naming.NamingScheme;

public final class NamingSchemeUtils {
    public static NamingScheme getNamingScheme(NameConvention annotation, BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
        String customSchemeName = annotation.custom();
        if (customSchemeName.isEmpty()) {
            return annotation.value();
        } else {
            NamingScheme customScheme;
            try {
                Class<?> customSchemeClass = Class.forName(customSchemeName);
                customScheme = (NamingScheme) customSchemeClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw exceptionFactory.apply("Couldn't find custom naming scheme class '" + customSchemeName + "'", e);
            } catch (InstantiationException | IllegalAccessException e) {
                throw exceptionFactory.apply("Couldn't create instance of custom name scheme class '" + customSchemeName + "'", e);
            } catch (ClassCastException e) {
                throw exceptionFactory.apply("Class '" + customSchemeName + "' does not implement '" + NamingScheme.class.getName() + "'", e);
            }
            return customScheme;
        }
    }
}
