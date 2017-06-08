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

import static java.util.Objects.requireNonNull;

import org.lanternpowered.launch.transformer.ClassTransformer;
import org.lanternpowered.launch.transformer.ClassTransformers;
import org.lanternpowered.launch.transformer.Exclusion;
import org.lanternpowered.server.LanternServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A {@link ClassLoader} that gives complete control over all the libraries used by
 * {@link LanternServer}. Mainly designed for Java 9+, the System ClassLoader no longer
 * extends {@link URLClassLoader}. This {@link ClassLoader} should be used instead.
 * <p>
 * All {@link Class#forName(String)} operations will be delegated through this
 * {@link ClassLoader}.
 */
public final class ServerClassLoader extends AbstractClassLoader {

    private static final ServerClassLoader classLoader;

    static {
        ClassLoader.registerAsParallelCapable();

        // All the folders are from lantern or sponge,
        // in development mode are all the libraries on
        // the classpath, so there is no need to add them
        // to the library classloader
        final List<URL> urls = new ArrayList<>();

        // If we are outside development mode, the server will be packed
        // into a jar. We will also need to make sure that this one gets
        // added in this case
        final CodeSource source = ServerClassLoader.class.getProtectionDomain().getCodeSource();
        final URL location = source == null ? null : source.getLocation();

        final String classPath = System.getProperty("java.class.path");
        final String[] libraries = classPath.split(File.pathSeparator);
        for (String library : libraries) {
            try {
                final URL url = Paths.get(library).toUri().toURL();
                if (!library.endsWith(".jar") || url.equals(location)) {
                    urls.add(url);
                }
            } catch (MalformedURLException ignored) {
                System.out.println("Invalid library found in the class path: " + library);
            }
        }

        final ClassLoader parent = ServerClassLoader.class.getClassLoader();
        final List<URL> libraryUrls = new ArrayList<>();
        libraryUrls.addAll(urls); // In case we need to fall back for some classes

        // Scan the jar for library jars
        if (location != null) {
            try (ZipInputStream is = new ZipInputStream(location.openStream())) {
                ZipEntry e;
                while ((e = is.getNextEntry()) != null) {
                    final String name = e.getName();
                    // Check if it's a library jar
                    if (name.startsWith("libraries") && name.endsWith(".jar")) {
                        // Yay
                        final URL url = parent.getResource(name);
                        requireNonNull(url, "Something funky happened");
                        final Path path = Paths.get(e.getName());
                        final Path p = path.getParent();
                        if (!Files.exists(p)) {
                            Files.createDirectories(p);
                            if (Files.exists(path)) {
                                Files.delete(path);
                            }
                        }

                        try (ReadableByteChannel i = Channels.newChannel(url.openStream());
                                FileOutputStream o = new FileOutputStream(path.toFile())) {
                            o.getChannel().transferFrom(i, 0, Long.MAX_VALUE);
                        }

                        libraryUrls.add(path.toUri().toURL());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // The library class loader will load all the libraries
        final LibraryClassLoader libraryClassLoader = new LibraryClassLoader(
                libraryUrls.toArray(new URL[libraryUrls.size()]), parent);
        // The server class loader will load lantern, the api and all the plugins
        final ServerClassLoader serverClassLoader = new ServerClassLoader(
                urls.toArray(new URL[urls.size()]), libraryClassLoader);

        classLoader = serverClassLoader;
        Thread.currentThread().setContextClassLoader(serverClassLoader);
    }

    /**
     * Gets the {@link ServerClassLoader}.
     *
     * @return The class loader
     */
    public static ServerClassLoader get() {
        return classLoader;
    }

    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    private final Set<String> invalidClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private ServerClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        ClassTransformers.get().addExclusion(Exclusion.forClass("com.google.common.io.ByteStreams"));
    }

    /**
     * Gets the {@link LibraryClassLoader}.
     *
     * @return The library class loader
     */
    public LibraryClassLoader getLibraryLoader() {
        return (LibraryClassLoader) getParent();
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
        // New classes are available, let the class loader try again
        this.invalidClasses.clear();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                // Ignore the launch package, this is the only package that will be loaded
                // through the system class loader
                if (name.startsWith("org.lanternpowered.launch.")) {
                    // This has to be found
                    c = getParent().getParent().loadClass(name);
                } else {
                    try {
                        c = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                    if (c == null) {
                        c = getParent().loadClass(name);
                    }
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
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
            final ByteBuffer byteBuffer = ByteBuffer.allocate(is.available());
            try (ReadableByteChannel input = Channels.newChannel(is)) {
                input.read(byteBuffer);
            }

            byte[] bytes = byteBuffer.array();
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
            e.printStackTrace();
            this.invalidClasses.add(name);
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    public URL getResource(String name) {
        final URL url = findResource(name);
        if (url != null) {
            return url;
        }
        return getParent().getResource(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        final Enumeration<URL>[] enumerations = new Enumeration[2];
        enumerations[0] = super.findResources(name);
        enumerations[1] = getLibraryLoader().findResources(name);
        return new Enumeration<URL>() {

            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                return this.index < enumerations.length && enumerations[this.index].hasMoreElements();
            }

            @Override
            public URL nextElement() {
                final URL next = enumerations[this.index].nextElement();
                if (!enumerations[this.index].hasMoreElements()) {
                    this.index++;
                }
                return next;
            }
        };
    }
}
