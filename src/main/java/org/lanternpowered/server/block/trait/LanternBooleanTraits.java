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
package org.lanternpowered.server.block.trait;

import org.lanternpowered.server.data.key.LanternKeys;
import org.spongepowered.api.block.trait.BooleanTrait;
import org.spongepowered.api.data.key.Keys;

public final class LanternBooleanTraits {

    public static final BooleanTrait SNOWY = LanternBooleanTrait.of("snowy", Keys.SNOWED);

    public static final BooleanTrait DECAYABLE = LanternBooleanTrait.of("decayable", Keys.DECAYABLE);

    public static final BooleanTrait CHECK_DECAY = LanternBooleanTrait.of("check_decay", LanternKeys.CHECK_DECAY);

    public static final BooleanTrait IS_WET = LanternBooleanTrait.of("wet", Keys.IS_WET);

    public static final BooleanTrait OCCUPIED = LanternBooleanTrait.of("occupied", Keys.OCCUPIED);

    public static final BooleanTrait SEAMLESS = LanternBooleanTrait.of("seamless", Keys.SEAMLESS);

    public static final BooleanTrait ENABLED = LanternBooleanTrait.of("enabled", LanternKeys.ENABLED);

    public static final BooleanTrait TRIGGERED = LanternBooleanTrait.of("triggered", LanternKeys.TRIGGERED);

    public static final BooleanTrait POWERED = LanternBooleanTrait.of("powered", Keys.POWERED);

    public static final BooleanTrait EXPLODE = LanternBooleanTrait.of("explode", LanternKeys.EXPLODE);

    public static final BooleanTrait HAS_RECORD = LanternBooleanTrait.of("has_record", LanternKeys.HAS_RECORD);
}
