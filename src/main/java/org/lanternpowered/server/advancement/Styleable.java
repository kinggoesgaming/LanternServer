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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public abstract class Styleable extends PluginCatalogType.Base {

    private final Text title;
    private final Text description;
    private final ItemStackSnapshot icon;
    private final FrameType frameType;
    private final boolean showToast;

    Styleable(String pluginId, String id, String name, Text title, Text description,
            ItemStackSnapshot icon, FrameType frameType, boolean showToast) {
        super(pluginId, id, name);
        checkNotNull(title, "title");
        checkNotNull(description, "description");
        checkNotNull(icon, "icon");
        checkNotNull(frameType, "frameType");
        this.description = description;
        this.frameType = frameType;
        this.showToast = showToast;
        this.title = title;
        this.icon = icon;
    }

    /**
     * Gets the description.
     *
     * @return The description
     */
    public Text getDescription() {
        return this.description;
    }

    /**
     * Gets the icon.
     *
     * @return The icon
     */
    public ItemStackSnapshot getIcon() {
        return this.icon;
    }

    /**
     * Gets the {@link FrameType}.
     *
     * @return The frame type
     */
    public FrameType getFrameType() {
        return this.frameType;
    }

    /**
     * Gets the title.
     *
     * @return The title
     */
    public Text getTitle() {
        return this.title;
    }

    /**
     * Gets whether a toast should be shown. This is the notification
     * that will be displayed in the top right corner.
     *
     * @return Show toast
     */
    public boolean doesShowToast() {
        return this.showToast;
    }
}
