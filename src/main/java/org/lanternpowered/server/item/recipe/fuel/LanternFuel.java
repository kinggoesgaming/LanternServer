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
package org.lanternpowered.server.item.recipe.fuel;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.OptionalInt;

final class LanternFuel extends PluginCatalogType.Base implements IFuel {

    final IFuelBurnTimeProvider burnTimeProvider;
    private final Ingredient ingredient;

    LanternFuel(String pluginId, String name,
            IFuelBurnTimeProvider burnTimeProvider,
            Ingredient ingredient) {
        super(pluginId, name);
        this.burnTimeProvider = burnTimeProvider;
        this.ingredient = ingredient;
    }

    @Override
    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public OptionalInt getBurnTime(ItemStackSnapshot input) {
        return isValid(input) ? OptionalInt.of(this.burnTimeProvider.get(input)) : OptionalInt.empty();
    }

    @Override
    public OptionalInt getBurnTime(ItemStack input) {
        return isValid(input) ? OptionalInt.of(this.burnTimeProvider.get(input)) : OptionalInt.empty();
    }
}
