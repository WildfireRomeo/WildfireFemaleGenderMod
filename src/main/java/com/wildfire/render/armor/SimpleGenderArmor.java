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

package com.wildfire.render.armor;

import com.wildfire.api.IGenderArmor;

/**
 * Default implementations of {@link IGenderArmor} for vanilla armor types
 */
public record SimpleGenderArmor(float physicsResistance, float tightness, boolean armorStandsCopySettings) implements IGenderArmor {

    public static final SimpleGenderArmor FALLBACK = new SimpleGenderArmor(0.5F);
    public static final SimpleGenderArmor LEATHER = new SimpleGenderArmor(0.3F, 0.5F);
    public static final SimpleGenderArmor CHAIN_MAIL = new SimpleGenderArmor(0.5F, 0.2F);
    public static final SimpleGenderArmor GOLD = new SimpleGenderArmor(0.85F, true);
    public static final SimpleGenderArmor IRON = new SimpleGenderArmor(1, true);
    public static final SimpleGenderArmor DIAMOND = new SimpleGenderArmor(1, true);
    public static final SimpleGenderArmor NETHERITE = new SimpleGenderArmor(1, true);

    public SimpleGenderArmor(float physicsResistance) {
        this(physicsResistance, 0, false);
    }

    public SimpleGenderArmor(float physicsResistance, boolean armorStandsCopySettings) {
        this(physicsResistance, 0f, armorStandsCopySettings);
    }

    public SimpleGenderArmor(float physicsResistance, float tightness) {
        this(physicsResistance, tightness, false);
    }
}