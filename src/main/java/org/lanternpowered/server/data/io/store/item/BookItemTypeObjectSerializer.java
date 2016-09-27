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
package org.lanternpowered.server.data.io.store.item;

import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.gson.JsonTextTranslatableSerializer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Locale;
import java.util.stream.Collectors;

public class BookItemTypeObjectSerializer implements ItemTypeObjectSerializer {

    private static final DataQuery AUTHOR = DataQuery.of("author");
    private static final DataQuery TITLE = DataQuery.of("title");
    private static final DataQuery PAGES = DataQuery.of("pages");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        valueContainer.get(Keys.BOOK_AUTHOR).ifPresent(text ->
                dataView.set(AUTHOR, LanternTexts.toLegacy(text)));
        valueContainer.get(Keys.BOOK_PAGES).ifPresent(lines ->
                dataView.set(PAGES, lines.stream().map(TextSerializers.JSON::serialize).collect(Collectors.toList())));
        valueContainer.get(Keys.DISPLAY_NAME).ifPresent(text ->
                dataView.set(TITLE, LanternTexts.toLegacy(text)));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
    }

    public static void writeBookData(DataView dataView, BookView bookView, Locale locale) {
        dataView.set(AUTHOR, LanternTexts.toLegacy(bookView.getAuthor()));
        dataView.set(TITLE, LanternTexts.toLegacy(bookView.getTitle()));
        JsonTextTranslatableSerializer.setCurrentLocale(locale);
        dataView.set(PAGES, bookView.getPages().stream().map(Types.TEXT_GSON::toJson).collect(Collectors.toList()));
        JsonTextTranslatableSerializer.removeCurrentLocale();
    }
}