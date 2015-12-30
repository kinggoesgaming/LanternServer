/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.inject.impl.reflect;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.lanternpowered.server.inject.Injector;
import org.lanternpowered.server.inject.InjectorFactory;
import org.lanternpowered.server.inject.Module;

import java.util.concurrent.ExecutionException;

public final class ReflectInjectorFactory implements InjectorFactory {

    private final static ReflectInjectorFactory instance = new ReflectInjectorFactory();

    public static ReflectInjectorFactory instance() {
        return instance;
    }

    private final LoadingCache<Module, Injector> cache =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Module, Injector>() {
                @Override
                public Injector load(Module module) throws Exception {
                    return new ReflectInjector(module);
                }
            });

    @Override
    public Injector create(Module module) {
        checkNotNull(module, "module");
        try {
            return this.cache.get(module);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
