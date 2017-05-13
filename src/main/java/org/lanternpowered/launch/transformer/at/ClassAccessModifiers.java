package org.lanternpowered.launch.transformer.at;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

final class ClassAccessModifiers {

    @Nullable final AccessModifier modifier;
    @Nullable private final AccessModifier fieldModifier;
    @Nullable private final AccessModifier methodModifier;

    private final ImmutableMap<String, AccessModifier> fields;
    private final ImmutableMap<String, AccessModifier> methods;

    private ClassAccessModifiers(@Nullable AccessModifier modifier, @Nullable AccessModifier fieldModifier, @Nullable AccessModifier methodModifier,
            ImmutableMap<String, AccessModifier> fields, ImmutableMap<String, AccessModifier> methods) {
        this.modifier = modifier;
        this.fieldModifier = fieldModifier;
        this.methodModifier = methodModifier;
        this.fields = fields;
        this.methods = methods;
    }

    @Nullable
    AccessModifier getField(String name) {
        AccessModifier modifier = this.fields.get(name);
        return modifier != null ? modifier : this.fieldModifier;
    }

    @Nullable
    AccessModifier getMethod(String name, String desc) {
        return getMethod(name.concat(desc));
    }

    @Nullable
    private AccessModifier getMethod(String identifier) {
        AccessModifier modifier = this.methods.get(identifier);
        return modifier != null ? modifier : this.methodModifier;
    }

    static final class Builder {

        @Nullable private AccessModifier modifier;
        @Nullable private AccessModifier fieldModifier;
        @Nullable private AccessModifier methodModifier;

        private final Map<String, AccessModifier> fields = new HashMap<>();
        private final Map<String, AccessModifier> methods = new HashMap<>();

        void applyToClass(AccessModifier modifier) {
            this.modifier = modifier.merge(this.modifier);
        }

        void applyToFields(AccessModifier modifier) {
            this.fieldModifier = modifier.merge(this.fieldModifier);
        }

        void applyToMethods(AccessModifier modifier) {
            this.methodModifier = modifier.merge(this.methodModifier);
        }

        void applyToField(String name, AccessModifier modifier) {
            fields.put(name, modifier.merge(fields.get(name)));
        }

        void applyToMethod(String identifier, AccessModifier modifier) {
            methods.put(identifier, modifier.merge(methods.get(identifier)));
        }

        private static ImmutableMap<String, AccessModifier> build(Map<String, AccessModifier> map, @Nullable AccessModifier base) {
            final ImmutableMap.Builder<String, AccessModifier> builder = ImmutableMap.builder();
            for (Map.Entry<String, AccessModifier> entry : map.entrySet()) {
                builder.put(entry.getKey(), entry.getValue().merge(base));
            }
            return builder.build();
        }

        ClassAccessModifiers build() {
            return new ClassAccessModifiers(this.modifier, this.fieldModifier, this.methodModifier,
                    build(this.fields, this.fieldModifier),
                    build(this.methods, this.methodModifier));
        }
    }

}
