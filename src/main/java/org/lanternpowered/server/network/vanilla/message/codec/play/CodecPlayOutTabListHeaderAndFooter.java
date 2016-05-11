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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.spongepowered.api.text.Text;

public final class CodecPlayOutTabListHeaderAndFooter implements Codec<MessagePlayOutTabListHeaderAndFooter> {

    // This is the only text type that can be empty on the client
    // for the result of #getFormattedText
    private static final String EMPTY_TEXT = "{\"translate\":\"\"}";

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTabListHeaderAndFooter message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        Text header = message.getHeader();
        Text footer = message.getFooter();
        if (header != null) {
            buf.write(Types.TEXT, header);
        } else {
            buf.writeString(EMPTY_TEXT);
        }
        if (footer != null) {
            buf.write(Types.TEXT, footer);
        } else {
            buf.writeString(EMPTY_TEXT);
        }
        return buf;
    }
}
