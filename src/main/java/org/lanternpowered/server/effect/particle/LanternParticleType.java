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
package org.lanternpowered.server.effect.particle;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;

public class LanternParticleType extends SimpleCatalogType.Base implements ParticleType {

    private final String vanillaId;
    private final boolean hasMotion;
    private final int internalId;

    public LanternParticleType(int internalId, String identifier, boolean hasMotion) {
        this(internalId, identifier, identifier, hasMotion);
    }

    public LanternParticleType(int internalId, String identifier, String vanillaId, boolean hasMotion) {
        super(identifier);
        this.vanillaId = checkNotNullOrEmpty(vanillaId, "vanillaId");
        this.internalId = internalId;
        this.hasMotion = hasMotion;
    }

    @Override
    public boolean hasMotion() {
        return this.hasMotion;
    }

    /**
     * Gets the internal id of the particle type used in networking.
     *
     * @return The internal id
     */
    public int getInternalId() {
        return this.internalId;
    }

    /**
     * Gets the id of the particle type used in the vanilla game.
     *
     * @return The vanilla id
     */
    public String getVanillaId() {
        return this.vanillaId;
    }

    public static class Item extends LanternParticleType implements ParticleType.Item {

        private final ItemStack defaultItem;

        public Item(int internalId, String name, boolean hasMotion, ItemStack defaultItem) {
            this(internalId, name, name, hasMotion, defaultItem);
        }

        public Item(int internalId, String name, String vanillaId, boolean hasMotion, ItemStack defaultItem) {
            super(internalId, name, vanillaId, hasMotion);
            this.defaultItem = defaultItem;
        }

        @Override
        public ItemStack getDefaultItem() {
            return this.defaultItem.copy();
        }

    }

    public static class Block extends LanternParticleType implements ParticleType.Block {

        private final BlockState defaultBlockState;

        public Block(int internalId, String name, boolean hasMotion, BlockState defaultBlockState) {
            this(internalId, name, name, hasMotion, defaultBlockState);
        }

        public Block(int internalId, String name, String vanillaId, boolean hasMotion, BlockState defaultBlockState) {
            super(internalId, name, vanillaId, hasMotion);
            this.defaultBlockState = defaultBlockState;
        }

        @Override
        public BlockState getDefaultBlockState() {
            return this.defaultBlockState;
        }

    }

    public static class Colorable extends LanternParticleType implements ParticleType.Colorable {

        private final Color defaultColor;

        public Colorable(int internalId, String name, boolean hasMotion, Color defaultColor) {
            this(internalId, name, name, hasMotion, defaultColor);
        }

        public Colorable(int internalId, String name, String vanillaId, boolean hasMotion, Color defaultColor) {
            super(internalId, name, vanillaId, hasMotion);
            this.defaultColor = defaultColor;
        }

        @Override
        public Color getDefaultColor() {
            return this.defaultColor;
        }

    }

    public static class Resizable extends LanternParticleType implements ParticleType.Resizable {

        private final float defaultSize;

        public Resizable(int internalId, String name, boolean hasMotion, float defaultSize) {
            this(internalId, name, name, hasMotion, defaultSize);
        }

        public Resizable(int internalId, String name, String vanillaId, boolean hasMotion, float defaultSize) {
            super(internalId, name, vanillaId, hasMotion);
            this.defaultSize = defaultSize;
        }

        @Override
        public float getDefaultSize() {
            return this.defaultSize;
        }

    }

    public static class Note extends LanternParticleType implements ParticleType.Note {

        private final NotePitch defaultNote;

        public Note(int internalId, String name, boolean hasMotion, NotePitch defaultNote) {
            this(internalId, name, name, hasMotion, defaultNote);
        }

        public Note(int internalId, String name, String vanillaId, boolean hasMotion, NotePitch defaultNote) {
            super(internalId, name, vanillaId, hasMotion);
            this.defaultNote = defaultNote;
        }

        @Override
        public NotePitch getDefaultNote() {
            return this.defaultNote;
        }

    }

}
