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

import org.lanternpowered.server.block.LanternBlockTypes;
import org.lanternpowered.server.effect.sound.LanternSoundTypes;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

public class LanternShulkerBox extends LanternContainer<TileShulkerBoxInventory> {

    private final Random random = new Random();

    @Override
    public void registerKeys() {
        super.registerKeys();
        this.registerKey(Keys.DISPLAY_NAME, null);
    }

    @Override
    protected TileShulkerBoxInventory createInventory() {
        return new TileShulkerBoxInventory(null, this);
    }

    @Override
    protected void playOpenSound(Location<World> location) {
        location.getExtent().playSound(LanternSoundTypes.BLOCK_SHULKER_BOX_OPEN, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, this.random.nextDouble() * 0.1 + 0.9);
    }

    @Override
    protected void playCloseSound(Location<World> location) {
        location.getExtent().playSound(LanternSoundTypes.BLOCK_SHULKER_BOX_CLOSE, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, this.random.nextDouble() * 0.1 + 0.9);
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = this.getLocation().getBlock();
        // TODO: Check the colors??
        return block.getType().getId().contains("shulker_box") ? block : LanternBlockTypes.WHITE_SHULKER_BOX.getDefaultState();
    }
}
