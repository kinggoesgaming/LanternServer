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
package org.lanternpowered.server.block.tile.vanilla;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Jukebox;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import javax.annotation.Nullable;

public class LanternJukebox extends LanternTileEntity implements Jukebox {

    @Nullable private ItemStackSnapshot record;
    private boolean playing;

    @Override
    public void playRecord() {
        this.playing = true;
    }

    /**
     * Whether currently a record is being played.
     *
     * @return Is playing
     */
    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    public void stopRecord() {
        this.playing = false;
    }

    @Override
    public void ejectRecord() {
        this.record = null;
        // TODO: Drop the item
    }

    /**
     * Resets the record {@link ItemStackSnapshot} and
     * returns it. If present.
     *
     * @return The record item
     */
    public Optional<ItemStackSnapshot> ejectRecordItem() {
        try {
            return Optional.ofNullable(this.record);
        } finally {
            this.record = null;
        }
    }

    @Override
    public void insertRecord(ItemStack record) {
        checkNotNull(record, "record");
        this.record = record.createSnapshot();
    }

    @Override
    public BlockState getBlock() {
        return BlockTypes.JUKEBOX.getDefaultState();
    }
}
