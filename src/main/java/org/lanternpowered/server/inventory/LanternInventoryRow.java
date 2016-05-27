/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class LanternInventoryRow extends LanternInventory2D implements InventoryRow {

    public LanternInventoryRow(@Nullable Inventory parent) {
        super(parent, null);
    }

    public LanternInventoryRow(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    @Override
    protected <T extends Slot> T registerSlot(T slot) {
        throw new UnsupportedOperationException("Do not use this method directly when using a InventoryColumn, see registerSlotAt(x, slot)");
    }

    /**
     * Registers a regular {@link Slot} for this inventory.
     *
     * @param x The x position of the slot in this column
     * @return The slot for chaining
     * @throws IllegalStateException If there already a slot is registered
     *      at the specified x index
     */
    protected LanternSlot registerSlotAt(int x) {
        return this.registerSlotAt(x, new LanternSlot(this));
    }

    /**
     * Registers the {@link Slot} for this inventory at the
     * specified x index.
     *
     * @param x The x position of the slot in this row
     * @param slot The slot to register
     * @return The slot for chaining
     * @throws IllegalStateException If there already a slot is registered
     *      at the specified x index
     */
    protected <T extends Slot> T registerSlotAt(int x, T slot) {
        this.registerSlot(this.nextFreeSlotIndex(), x, slot);
        return slot;
    }

    /**
     * Registers the {@link Slot} for this inventory at the
     * specified x index.
     *
     * @param index The index of the slot
     * @param x The x position of the slot in this row
     * @param slot The slot to register
     * @return The slot for chaining
     */
    <T extends Slot> T registerSlot(int index, int x, T slot) {
        checkNotNull(slot, "slot");
        checkArgument(x >= 0, "x position may not be negative");
        checkArgument(this.slots.size() <= index || this.slots.get(index) == null, "The slot index %s is already in use", index);
        checkArgument(!this.indexBySlot.containsKey(slot), "The slot is already registered");
        final Vector2i pos = new Vector2i(x, 0);
        checkArgument(!this.slotsByPos.containsKey(pos), "The slot position (%s;0) is already in use", x);
        this.registerSlot(index, slot, true);
        this.slotsByPos.put(pos, (LanternSlot) slot);
        return slot;
    }
}