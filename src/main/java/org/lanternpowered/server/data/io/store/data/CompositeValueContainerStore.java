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
package org.lanternpowered.server.data.io.store.data;

import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeValueContainerStore<T extends S, S extends CompositeValueStore<S, H>, H extends ValueContainer<?>> implements ObjectStore<T> {

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(T object, DataView dataView) {
        if (object instanceof AbstractValueContainer) {
            final AbstractValueContainer<S> valueContainer = (AbstractValueContainer) object;
            final SimpleValueContainer simpleValueContainer = new SimpleValueContainer(new HashMap<>());

            this.deserializeValues(object, simpleValueContainer, dataView);
            for (Map.Entry<Key<?>, Object> entry : simpleValueContainer.getValues().entrySet()) {
                final ElementHolder elementHolder = valueContainer.getElementHolder((Key) entry.getKey());
                if (elementHolder != null) {
                    elementHolder.set(entry.getValue());
                } else {
                    Lantern.getLogger().warn("Attempted to offer a unsupported value with key \"{}\" to the object {}",
                            entry.getKey().toString(), object.toString());
                }
            }

            final List<DataManipulator<?,?>> additionalManipulators = valueContainer.getRawAdditionalManipulators();
            if (additionalManipulators != null) {
                this.deserializeAdditionalData(object, additionalManipulators, dataView);
            }
        } else {
            // Not sure what to do
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(T object, DataView dataView) {
        if (object instanceof AbstractValueContainer) {
            final AbstractValueContainer<S> valueContainer = (AbstractValueContainer) object;
            final SimpleValueContainer simpleValueContainer = new SimpleValueContainer(new HashMap<>());

            for (Map.Entry<Key<?>, KeyRegistration> entry : valueContainer.getRawValueMap().entrySet()) {
                final Object value = entry.getValue();
                if (value instanceof ElementHolder) {
                    final Object element = ((ElementHolder) value).get();
                    if (element != null) {
                        simpleValueContainer.set((Key) entry.getKey(), element);
                    }
                }
            }

            this.serializeValues(object, simpleValueContainer, dataView);

            final List<DataManipulator<?,?>> additionalManipulators = valueContainer.getRawAdditionalManipulators();
            if (additionalManipulators != null) {
                this.serializeAdditionalData(object, additionalManipulators, dataView);
            }
        } else {
            // Not sure what to do
        }
    }

    /**
     * Serializes all the {@link DataManipulator}s and puts
     * them into the {@link DataView}.
     *
     * @param manipulators The data manipulators
     * @param dataView The data view
     */
    public void serializeAdditionalData(T object, List<DataManipulator<?, ?>> manipulators, DataView dataView) {
    }

    /**
     * Deserializes all the {@link DataManipulator}s from the {@link DataView}
     * and puts them into the {@link List}.
     *
     * @param manipulators The data manipulators
     * @param dataView The data view
     */
    public void deserializeAdditionalData(T object, List<DataManipulator<?, ?>> manipulators, DataView dataView) {
    }

    /**
     * Serializes all the values of the {@link SimpleValueContainer} and puts
     * them into the {@link DataView}.
     *
     * @param valueContainer The value container
     * @param dataView The data view
     */
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
    }

    /**
     * Deserializers all the values from the {@link DataView}
     * into the {@link SimpleValueContainer}.
     *
     * @param valueContainer The value container
     * @param dataView The data view
     */
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
    }
}
