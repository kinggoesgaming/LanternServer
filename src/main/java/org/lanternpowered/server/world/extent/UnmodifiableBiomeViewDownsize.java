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
package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.world.extent.worker.LanternBiomeAreaWorker;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.worker.BiomeAreaWorker;

public class UnmodifiableBiomeViewDownsize extends AbstractBiomeViewDownsize<BiomeArea> implements UnmodifiableBiomeArea {

    public UnmodifiableBiomeViewDownsize(BiomeArea area, Vector2i min, Vector2i max) {
        super(area, min, max);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        this.checkRange(newMin.getX(), newMin.getY());
        this.checkRange(newMax.getX(), newMax.getY());
        return new UnmodifiableBiomeViewDownsize(this.area, newMin, newMax);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new UnmodifiableBiomeViewTransform(this, transform);
    }

    @Override
    public BiomeAreaWorker<? extends UnmodifiableBiomeArea> getBiomeWorker() {
        return new LanternBiomeAreaWorker<>(this);
    }

}
