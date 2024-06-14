/*
    Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
    Copyright (C) 2023 WildfireRomeo

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.api;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.ItemCapability;

public final class WildfireAPI {

    public static final String MODID = "wildfire_gender";

    private WildfireAPI() {
    }

    /**
     * Item capability used for gender armor.
     */
    public static final ItemCapability<IGenderArmor, Void> GENDER_ARMOR_CAPABILITY = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(MODID, "gender_armor"), IGenderArmor.class);
}