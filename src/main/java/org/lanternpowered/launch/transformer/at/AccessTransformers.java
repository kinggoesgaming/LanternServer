package org.lanternpowered.launch.transformer.at;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class AccessTransformers {

    private AccessTransformers() {
    }

    private static final char COMMENT_PREFIX = '#';
    private static final Splitter SEPARATOR = Splitter.on(' ').trimResults().omitEmptyStrings();

    private static final char WILDCARD = '*';

    @Nullable private static Map<String, ClassAccessModifiers.Builder> rules = new HashMap<>();

    private static void verifyState() {
        checkState(rules != null, "Attempted to register after access transformer was initialized");
    }

    public static void register(Path path) throws IOException {
        verifyState();
        try (BufferedReader reader = Files.newBufferedReader(path, UTF_8)) {
            register(reader);
        }
    }

    public static void register(URL url) throws IOException {
        verifyState();
        try (InputStream in = url.openStream()) {
            register(in);
        }
    }

    public static void register(InputStream in) throws IOException {
        verifyState();
        register(new BufferedReader(new InputStreamReader(in, UTF_8)));
    }

    public static void register(BufferedReader reader) throws IOException {
        verifyState();
        String line;
        while ((line = reader.readLine()) != null) {
            line = substringBefore(line, COMMENT_PREFIX).trim();
            if (line.isEmpty()) {
                continue;
            }

            List<String> parts = SEPARATOR.splitToList(line);
            checkArgument(parts.size() <= 3, "Invalid access transformer config line: %s", line);

            AccessModifier modifier = null;

            String access = parts.get(0);
            if (access.startsWith("public")) {
                modifier = AccessModifier.PUBLIC;
            } else if (access.startsWith("protected")) {
                modifier = AccessModifier.PROTECTED;
            }

            if (access.endsWith("-f")) {
                modifier = AccessModifier.REMOVE_FINAL.merge(modifier);
            }

            if (modifier == null) {
                continue;
            }

            final String className = parts.get(1);
            ClassAccessModifiers.Builder builder = rules.get(className);
            if (builder == null) {
                rules.put(className, builder = new ClassAccessModifiers.Builder());
            }

            if (parts.size() == 2) {
                // Class
                builder.applyToClass(modifier);
            } else {
                String name = parts.get(2);
                boolean method = name.indexOf('(') > 0;

                if (name.charAt(0) == WILDCARD) {
                    // Wildcard
                    if (method) {
                        builder.applyToMethods(modifier);
                    } else {
                        builder.applyToFields(modifier);
                    }
                } else if (method) {
                    builder.applyToMethod(name, modifier);
                } else {
                    builder.applyToField(name, modifier);
                }
            }
        }
    }

    private static String substringBefore(String s, char c) {
        int pos = s.indexOf(c);
        return pos >= 0 ? s.substring(0, pos) : s;
    }

    static ImmutableMap<String, ClassAccessModifiers> build() {
        final ImmutableMap.Builder<String, ClassAccessModifiers> builder = ImmutableMap.builder();
        for (Map.Entry<String, ClassAccessModifiers.Builder> entry : rules.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().build());
        }
        rules = null;
        return builder.build();
    }
}
