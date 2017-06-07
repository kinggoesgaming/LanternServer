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
package org.lanternpowered.launch.transformer;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ClassTransformers {

    private static final ClassTransformers classTransformers;

    static {
        classTransformers = new ClassTransformers();
    }

    public static ClassTransformers get() {
        return classTransformers;
    }

    private final List<ClassTransformer> transformers = new CopyOnWriteArrayList<>();
    private final List<ClassTransformer> unmodifiableTransformers = Collections.unmodifiableList(this.transformers);
    private final Set<Exclusion> exclusions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Exclusion> unmodifiableExclusions = Collections.unmodifiableSet(this.exclusions);

    /**
     * Gets all the {@link ClassTransformer}s.
     *
     * @return The class transformers
     */
    public List<ClassTransformer> getTransformers() {
        return this.unmodifiableTransformers;
    }

    /**
     * Gets all the {@link Exclusion}s.
     *
     * @return The exclusions
     */
    public Set<Exclusion> getExclusions() {
        return this.unmodifiableExclusions;
    }

    /**
     * Adds a {@link Exclusion}. All the excluded classes
     * will be skipped by the {@link ClassTransformer}s.
     *
     * @param exclusion The exclusion
     * @return This, for chaining
     */
    public ClassTransformers addExclusion(Exclusion exclusion) {
        requireNonNull(exclusion, "exclusion");
        this.exclusions.add(exclusion);
        return this;
    }

    /**
     * Adds the {@link Exclusion}s. All the excluded classes
     * will be skipped by the {@link ClassTransformer}s.
     *
     * @param exclusions The exclusions
     * @return This, for chaining
     */
    public ClassTransformers addExclusions(Exclusion... exclusions) {
        requireNonNull(exclusions, "exclusions");
        this.exclusions.addAll(Arrays.asList(exclusions));
        return this;
    }

    /**
     * Adds a new {@link ClassTransformer}.
     *
     * @param classTransformer The class transformer
     */
    public ClassTransformers addTransformer(ClassTransformer classTransformer) {
        requireNonNull(classTransformer, "classTransformer");
        this.transformers.add(classTransformer);
        // All the transformer classes should be excluded
        this.exclusions.add(Exclusion.forClass(classTransformer.getClass().getName(), true));
        return this;
    }

    private ClassTransformers() {
    }
}
