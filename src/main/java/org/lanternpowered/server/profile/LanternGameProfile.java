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
package org.lanternpowered.server.profile;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.persistence.DataBuilder;
import org.spongepowered.api.util.persistence.InvalidDataException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

@ConfigSerializable
public final class LanternGameProfile implements GameProfile {

    private static final DataQuery NAME = DataQuery.of("Name");
    private static final DataQuery UNIQUE_ID = DataQuery.of("UniqueId");
    private static final DataQuery PROPERTIES = DataQuery.of("Properties");
    private static final DataQuery VALUE = DataQuery.of("Value");
    private static final DataQuery SIGNATURE = DataQuery.of("Signature");

    @Setting("properties")
    private List<Property> properties;

    @Setting("uniqueId")
    private UUID uniqueId;

    @Setting("name")
    private String name;

    protected LanternGameProfile() {
    }

    public LanternGameProfile(UUID uniqueId, String name) {
        this(uniqueId, name, Lists.newArrayList());
    }

    public LanternGameProfile(UUID uniqueId, String name, List<Property> properties) {
        this.properties = ImmutableList.copyOf(checkNotNull(properties, "properties"));
        this.uniqueId = checkNotNull(uniqueId, "uniqueId");
        this.name = checkNotNullOrEmpty(name, "name");
    }

    /**
     * Creates a new game profile without all the properties.
     * 
     * @return the new game profile
     */
    public LanternGameProfile withoutProperties() {
        if (this.properties.isEmpty()) {
            return this;
        }
        return new LanternGameProfile(this.uniqueId, this.name);
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = new MemoryDataContainer()
                .set(NAME, this.name)
                .set(UNIQUE_ID, this.uniqueId.toString());
        if (!this.properties.isEmpty()) {
            List<DataContainer> list = Lists.newArrayListWithCapacity(this.properties.size());
            for (Property property : this.properties) {
                DataContainer entry = new MemoryDataContainer()
                       .set(NAME, property.name)
                       .set(VALUE, property.value);
                if (property.signature != null) {
                    entry.set(SIGNATURE, property.signature);
                }
                list.add(entry);
            }
            container.set(PROPERTIES, list);
        }
        return container;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        final LanternGameProfile o = (LanternGameProfile) other;
        return this.name.equals(o.name) && this.uniqueId.equals(o.uniqueId) &&
                this.properties.equals(o.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uniqueId, this.name, this.properties);
    }

    public static class LanternDataBuilder implements DataBuilder<GameProfile> {

        // Why use both the optional and data exception?
        @Override
        public Optional<GameProfile> build(DataView container) throws InvalidDataException {
            String uniqueId = container.getString(UNIQUE_ID).orElse(null);
            if (uniqueId == null) {
                throw new InvalidDataException("UniqueId is missing!");
            }
            UUID uniqueId0;
            try {
                uniqueId0 = UUID.fromString(uniqueId);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Unknown uniqueId format!", e);
            }
            String name = container.getString(NAME).orElse(null);
            if (name == null) {
                throw new InvalidDataException("Name is missing!");
            }
            List<DataView> views = container.getViewList(PROPERTIES).orElse(null);
            List<Property> properties;
            if (views != null && !views.isEmpty()) {
                properties = Lists.newArrayListWithCapacity(views.size());
                for (DataView view : views) {
                    if (view.contains(NAME) && view.contains(VALUE)) {
                        properties.add(new Property(view.getString(NAME).get(), view.getString(VALUE).get(),
                                view.getString(SIGNATURE).orElse(null)));
                    }
                }
            } else {
                properties = Lists.newArrayList();
            }
            return Optional.of(new LanternGameProfile(uniqueId0, name, properties));
        }
    }

    @ConfigSerializable
    public static final class Property {

        @Setting(value = "name")
        private String name;

        @Setting(value = "value")
        private String value;

        @Nullable
        @Setting(value = "signature")
        private String signature;

        protected Property() {
        }

        public Property(String name, String value, @Nullable String signature) {
            this.signature = signature;
            this.value = value;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        @Nullable
        public String getSignature() {
            return this.signature;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            final Property o = (Property) other;
            return this.name.equals(o.name) && this.value.equals(o.value) && Objects.equals(
                    this.signature, o.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.value, this.signature);
        }
    }
}
