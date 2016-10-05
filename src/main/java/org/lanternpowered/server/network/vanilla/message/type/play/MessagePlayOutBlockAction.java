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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutBlockAction implements Message {

    private final Vector3i position;
    private final int blockType;
    private final int parameterA;
    private final int parameterB;

    public MessagePlayOutBlockAction(Vector3i position, int blockType, int parameterA, int parameterB) {
        this.blockType = blockType;
        this.position = position;
        this.parameterA = parameterA;
        this.parameterB = parameterB;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public int getBlockType() {
        return this.blockType;
    }

    public int getParameterA() {
        return this.parameterA;
    }

    public int getParameterB() {
        return this.parameterB;
    }
}