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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class LanternItemStackBuilder extends AbstractDataBuilder<ItemStack> implements ItemStack.Builder {

    private LanternItemStack itemStack;

    public LanternItemStackBuilder() {
        super(ItemStack.class, 1);
    }

    @Override
    public ItemStack.Builder reset() {
        this.itemStack = null;
        return this;
    }

    @Override
    public ItemStack.Builder from(ItemStack value) {
        this.itemStack = (LanternItemStack) value.copy();
        return this;
    }

    @Override
    public ItemStack.Builder itemType(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        this.itemStack = new LanternItemStack(itemType);
        return this;
    }

    @Override
    public ItemType getCurrentItem() {
        return this.itemStack == null ? ItemTypes.NONE : this.itemStack.getItem();
    }

    @Override
    public ItemStack.Builder quantity(int quantity) throws IllegalArgumentException {
        checkNotNull(this.itemStack, "Cannot set quantity without having set a type first!");
        this.itemStack.setQuantity(quantity);
        return this;
    }

    @Override
    public <E> ItemStack.Builder keyValue(Key<? extends BaseValue<E>> key, E value) {
        checkNotNull(this.itemStack, "Cannot set item data without having set a type first!");
        this.itemStack.offer(key, value);
        return this;
    }

    @Override
    public ItemStack.Builder itemData(DataManipulator<?, ?> itemData) throws IllegalArgumentException {
        checkNotNull(this.itemStack, "Cannot add item data without having set a type first!");
        this.itemStack.offer(itemData);
        return this;
    }

    @Override
    public ItemStack.Builder itemData(ImmutableDataManipulator<?, ?> itemData) throws IllegalArgumentException {
        checkNotNull(this.itemStack, "Cannot add item data without having set a type first!");
        this.itemStack.offer(itemData.asMutable());
        return this;
    }

    @Override
    public <V> ItemStack.Builder add(Key<? extends BaseValue<V>> key, V value) throws IllegalArgumentException {
        checkNotNull(this.itemStack, "Cannot set item data without having set a type first!");
        this.itemStack.offer(key, value);
        return this;
    }

    @Override
    public ItemStack.Builder fromItemStack(ItemStack itemStack) {
        this.itemStack = (LanternItemStack) itemStack.copy();
        return this;
    }

    @Override
    public ItemStack.Builder fromContainer(DataView container) {
        return this; // TODO
    }

    @Override
    public ItemStack.Builder fromSnapshot(ItemStackSnapshot snapshot) {
        this.itemStack = (LanternItemStack) snapshot.createStack();
        return this;
    }

    @Override
    public ItemStack.Builder fromBlockSnapshot(BlockSnapshot blockSnapshot) {
        return this; // TODO
    }

    @Override
    public ItemStack.Builder remove(Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        checkNotNull(this.itemStack, "Cannot remove item data without having set a type first!");
        this.itemStack.remove(manipulatorClass);
        return this;
    }

    @Override
    public ItemStack build() throws IllegalStateException {
        checkNotNull(this.itemStack, "The item type must be set");
        return this.itemStack.copy();
    }

    @Override
    protected Optional<ItemStack> buildContent(DataView container) throws InvalidDataException {
        return null;
    }
}
