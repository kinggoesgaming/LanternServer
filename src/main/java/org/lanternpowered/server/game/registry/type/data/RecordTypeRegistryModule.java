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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.record.LanternRecordType;
import org.lanternpowered.server.data.type.record.RecordType;
import org.lanternpowered.server.data.type.record.RecordTypes;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;

public class RecordTypeRegistryModule extends InternalPluginCatalogRegistryModule<RecordType> {

    private static final RecordTypeRegistryModule INSTANCE = new RecordTypeRegistryModule();

    public static RecordTypeRegistryModule get() {
        return INSTANCE;
    }

    private RecordTypeRegistryModule() {
        super(RecordTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternRecordType("minecraft", "thirteen", "item.record.13.name", 0));
        register(new LanternRecordType("minecraft", "cat", "item.record.cat.name", 1));
        register(new LanternRecordType("minecraft", "blocks", "item.record.blocks.name", 2));
        register(new LanternRecordType("minecraft", "chirp", "item.record.chirp.name", 3));
        register(new LanternRecordType("minecraft", "far", "item.record.far.name", 4));
        register(new LanternRecordType("minecraft", "mall", "item.record.mall.name", 5));
        register(new LanternRecordType("minecraft", "mellohi", "item.record.mellohi.name", 6));
        register(new LanternRecordType("minecraft", "stal", "item.record.stal.name", 7));
        register(new LanternRecordType("minecraft", "strad", "item.record.strad.name", 8));
        register(new LanternRecordType("minecraft", "ward", "item.record.ward.name", 9));
        register(new LanternRecordType("minecraft", "eleven", "item.record.11.name", 10));
        register(new LanternRecordType("minecraft", "wait", "item.record.wait.name", 11));
    }
}
