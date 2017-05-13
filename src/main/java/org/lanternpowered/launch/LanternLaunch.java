/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.launch;

import org.lanternpowered.launch.transformer.ClassTransformers;
import org.lanternpowered.launch.transformer.Exclusion;
import org.lanternpowered.launch.transformer.at.AccessTransformer;
import org.lanternpowered.launch.transformer.at.AccessTransformers;

import java.io.IOException;

public final class LanternLaunch {

    public static void main(String[] args) {
        // Initialize the class loader
        final LanternClassLoader classLoader = LanternClassLoader.get();

        final ClassTransformers transformers = ClassTransformers.get();
        transformers.addExclusions(
                Exclusion.forPackage("groovy"),
                Exclusion.forPackage("groovyjarjarasm"),
                Exclusion.forPackage("groovyjarjarantlr"),
                Exclusion.forPackage("ninja.leaping.configurate"),
                Exclusion.forPackage("com.google"),
                Exclusion.forPackage("com.typesafe.config"),
                Exclusion.forPackage("com.flowpowered.noise"),
                Exclusion.forPackage("com.flowpowered.math"),
                Exclusion.forPackage("com.zaxxer.hikari"),
                Exclusion.forPackage("org.codehaus.groovy"),
                Exclusion.forPackage("org.yaml.snakeyaml"),
                Exclusion.forPackage("org.sqlite"),
                Exclusion.forPackage("org.mariadb.jdbc"),
                Exclusion.forPackage("org.objectweb.asm"),
                Exclusion.forPackage("org.apache"),
                Exclusion.forPackage("org.aopalliance"),
                Exclusion.forPackage("org.fusesource"),
                Exclusion.forPackage("io.netty"),
                Exclusion.forPackage("jline"),
                Exclusion.forPackage("it.unimi.dsi.fastutil"));

        try {
            AccessTransformers.register(LanternLaunch.class.getResourceAsStream("/internal/api_at.cfg"));
            AccessTransformers.register(LanternLaunch.class.getResourceAsStream("/internal/impl_at.cfg"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        transformers.addTransformer(new AccessTransformer());

        try {
            final Class<?> serverLaunchClass = classLoader.forName("org.lanternpowered.server.LanternServerLaunch", true);
            final Object serverLaunch = serverLaunchClass.newInstance();
            serverLaunchClass.getMethod("main", String[].class).invoke(serverLaunch, new Object[] { args });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LanternLaunch() {
    }
}
