/*
 * Copyright 2022-2024 QuiltMC
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

/**
 * Allows {@link org.quiltmc.config.api.ReflectiveConfig reflective configs} to add callbacks and configure their tracked values as they're modified.
 * <p>
 * Supports config classes and config fields, including tracked values and sections. To use a processor, specify a method name as the value, and create that method inside your config class.
 * </p>
 * Depending on the type it's attached to, it will take a different parameter.
 * <ul>
 *     <li>When used on a tracked value, the processor method will take a {@link org.quiltmc.config.api.values.TrackedValue.Builder} as its parameter.</li>
 *     <li>When used on a section (be careful to put the annotation on your section field, and not the class!), the processor method will take a {@link org.quiltmc.config.api.Config.SectionBuilder} as its parameter.</li>
 *     <li>When used on a config class, the processor method will take a {@link org.quiltmc.config.api.Config.Builder} as its parameter.</li>
 * </ul>
 *
 * <h3>Examples:</h3>
 * Tracked value:
 * <pre>
 * {@code
 * @Processor("processMaximum")
 * public final TrackedValue<Integer> maximum = this.value(10);
 *
 * public void processMaximum(TrackedValue.Builder<Integer> builder) {
 *     System.out.println("Processing 'maximum' field!");
 * }
 * }
 * </pre>
 *
 * <p>Config class:
 * <pre>
 * {@code
 * @Processor("processConfig")
 * public final class GreatReflectiveConfig extends ReflectiveConfig {
 *     public void processConfig(Config.Builder builder) {
 *         System.out.println("Processing config!");
 *     }
 * }
 * }
 * </pre>
 *
 * <p>Section:
 * <pre>
 * {@code
 * @Processor("processCoolSection")
 * public final CoolSection coolSection = new CoolSection();
 *
 * public void processCoolSection(SectionBuilder builder) {
 *     System.out.println("Processing 'coolSection' section!");
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Processor {
	String value();
}
