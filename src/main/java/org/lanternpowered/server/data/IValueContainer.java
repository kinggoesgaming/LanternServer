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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.processor.ValueProcessorKeyRegistration;
import org.lanternpowered.server.data.processor.Processor;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.ValueHelper;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;
import java.util.Set;

public interface IValueContainer<C extends ValueContainer<C>> extends ValueContainer<C>, IValueHolder {

    @Override
    default <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        return IValueHolder.super.getValueFor(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Key<?> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<?>, ?>) localKeyRegistration).isApplicableTo(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<?>, ?>) globalRegistration.get()).isApplicableTo(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            for (ValueContainer<?> valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        final KeyRegistration<BaseValue<E>, E> localKeyRegistration = getValueCollection().get(key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<E>, E>) localKeyRegistration).getFrom(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration<BaseValue<E>, E>> globalRegistration = LanternValueFactory.get().getKeyRegistration(key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<E>, E>) globalRegistration.get()).getFrom(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            for (ValueContainer<?> valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    return valueContainer.get(key);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E, V extends BaseValue<E>> Optional<V> getRawValueFor(Key<V> key) {
        // Check the local key registration
        final KeyRegistration<BaseValue<E>, E> localKeyRegistration = getValueCollection().get(key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<V, E>) localKeyRegistration).getValueFrom(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration<V, E>> globalRegistration = LanternValueFactory.get().getKeyRegistration(key);
        if (globalRegistration.isPresent()) {
            return ((Processor<V, E>) globalRegistration.get()).getValueFrom(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            for (ValueContainer<?> valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    return valueContainer.getValue(key);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<Key<?>> getKeys() {
        final ImmutableSet.Builder<Key<?>> keys = ImmutableSet.builder();

        // Check local registrations
        keys.addAll(getValueCollection().getKeys());

        // Check for global registrations
        LanternValueFactory.get().getKeyRegistrations().stream()
                .filter(registration -> ((Processor<BaseValue<?>, ?>) registration).isApplicableTo(this))
                .forEach(registration -> keys.add(registration.getKey()));

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            containers.getAll().forEach(manipulator -> keys.addAll(manipulator.getKeys()));
        }

        return keys.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<ImmutableValue<?>> getValues() {
        final ImmutableSet.Builder<ImmutableValue<?>> values = ImmutableSet.builder();

        // Check local registrations
        for (KeyRegistration<?,?> entry : getValueCollection().getAll()) {
            final Key key = entry.getKey();
            final Optional<BaseValue> optValue = getValue(key);
            optValue.ifPresent(baseValue -> values.add(ValueHelper.toImmutable(baseValue)));
        }

        // Check for global registrations
        for (ValueProcessorKeyRegistration<?,?> registration : LanternValueFactory.get().getKeyRegistrations()) {
            final Optional<BaseValue> optValue = ((Processor) registration).getValueFrom(this);
            optValue.ifPresent(baseValue -> values.add(ValueHelper.toImmutable(baseValue)));
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            containers.getAll().forEach(manipulator -> values.addAll(manipulator.getValues()));
        }

        return values.build();
    }

    /**
     * Gets the {@link ValueCollection}.
     *
     * @return The value collection
     */
    ValueCollection getValueCollection();
}
