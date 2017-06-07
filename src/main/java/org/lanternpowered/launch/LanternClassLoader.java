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

import com.google.common.io.ByteStreams;
import org.lanternpowered.launch.transformer.ClassTransformer;
import org.lanternpowered.launch.transformer.ClassTransformers;
import org.lanternpowered.launch.transformer.Exclusion;
import org.lanternpowered.server.LanternServer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link ClassLoader} that gives complete control over all the libraries used by
 * {@link LanternServer}. Mainly designed for Java 9+, the System ClassLoader no longer
 * extends {@link URLClassLoader}. This {@link ClassLoader} should be used instead.
 * <p>
 * All {@link Class#forName(String)} operations will be delegated through this
 * {@link ClassLoader}.
 */
public final class LanternClassLoader extends URLClassLoader {

    private static final LanternClassLoader classLoader;

    static {
        ClassLoader.registerAsParallelCapable();
        final List<URL> urls = new ArrayList<>();

        final String classPath = System.getProperty("java.class.path");
        final String[] libraries = classPath.split(";");
        for (String library : libraries) {
            if (!library.contains("java") && !library.contains("lib")) {
                try {
                    urls.add(Paths.get(library).toUri().toURL());
                } catch (MalformedURLException ignored) {
                    System.out.println("Invalid library found in the class path: " + library);
                }
            }
        }

        classLoader = new LanternClassLoader(urls.toArray(new URL[urls.size()]),
                ClassLoader.getSystemClassLoader());
        ClassTransformers.get().addExclusion(Exclusion.forClass("com.google.common.io.ByteStreams"));
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * Gets the {@link LanternClassLoader}.
     *
     * @return The class loader
     */
    public static LanternClassLoader get() {
        return classLoader;
    }

    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    private final Set<String> invalidClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Set<String> loaderExclusions = new HashSet<>(Arrays.asList(
            "org.lanternpowered.launch.",
            "org.objectweb.asm",
            "java.",
            "javax.",
            "sun."));

    private LanternClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * The same as {@link Class#forName(String, boolean, ClassLoader)},
     * but called for this {@link ClassLoader}.
     *
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public Class<?> forName(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, this);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                boolean flag = false;
                for (String loaderExclusion : this.loaderExclusions) {
                    if (name.startsWith(loaderExclusion)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    try {
                        c = findClass(name, resolve);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                if (c == null) {
                    c = getParent().loadClass(name);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    private Class<?> findClass(String name, boolean resolve) throws ClassNotFoundException {
        if (this.invalidClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }
        if (this.cachedClasses.containsKey(name)) {
            return this.cachedClasses.get(name);
        }
        final ClassTransformers transformers = ClassTransformers.get();
        for (Exclusion exclusion : transformers.getExclusions()) {
            if (exclusion.isApplicableFor(name)) {
                return super.findClass(name);
            }
        }
        final String fileName = name.replace('.', '/').concat(".class");
        final URL resource = findResource(fileName);
        if (resource == null) {
            this.invalidClasses.add(name);
            throw new ClassNotFoundException(name);
        }
        try {
            final int lastDot = name.lastIndexOf('.');
            final String packageName = lastDot == -1 ? "" : name.substring(0, lastDot);

            final Package pkg = getPackage(packageName);
            if (pkg == null) {
                definePackage(packageName, null, null, null, null, null, null, null);
            }

            final InputStream is = resource.openStream();
            byte[] bytes = new byte[is.available()];
            ByteStreams.readFully(is, bytes);
            is.close();

            final ClassReader reader = new ClassReader(bytes);
            reader.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public void visit(int version, int access, String name0, String signature, String superName, String[] interfaces) {
                    try {
                        final Class<?> superClass = loadClass(superName.replace('/', '.'), resolve);
                        final Class<?>[] interfaces0 = new Class<?>[interfaces.length];
                        for (int i = 0; i < interfaces.length; i++) {
                            interfaces0[i] = loadClass(interfaces[i].replace('/', '.'), resolve);
                        }
                        for (ClassTransformer transformer : transformers.getTransformers()) {
                            transformer.visit(LanternClassLoader.this, name, superClass, interfaces0);
                        }
                    } catch (ClassNotFoundException ignored) {
                        // Will be thrown later...
                    }
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            for (ClassTransformer transformer : transformers.getTransformers()) {
                try {
                    bytes = transformer.transform(this, name, bytes);
                } catch (Exception e) {
                    System.err.println("An error occurred while transforming " + name + ": " + e);
                }
            }

            final Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
            this.cachedClasses.put(name, clazz);
            return clazz;
        } catch (Throwable e) {
            this.invalidClasses.add(name);
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, false);
    }

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
            url = getParent().getResource(name);
        }
        return url;
    }
}
