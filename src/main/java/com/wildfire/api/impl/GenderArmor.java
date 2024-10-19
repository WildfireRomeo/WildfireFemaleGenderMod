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

package com.wildfire.api.impl;

import com.wildfire.api.IBreastArmorTexture;
import com.wildfire.api.IGenderArmor;

/**
 * Default record implementation of {@link IGenderArmor} used for resource pack entries
 *
 * @see IGenderArmor
 */
public record GenderArmor(
		float physicsResistance,
		float tightness,
		boolean coversBreasts,
		boolean alwaysHidesBreasts,
		boolean armorStandsCopySettings,
		IBreastArmorTexture texture
) implements IGenderArmor {
	/**
	 * Default implementation used to represent armor types that lack any configuration
	 */
	public static final IGenderArmor DEFAULT = new Default();

	/**
	 * Default implementation used when the player {@link net.minecraft.item.ItemStack#isEmpty() isn't wearing a chestplate},
	 * or if the worn chestplate specifies that it doesn't cover the breasts.
	 */
	public static final IGenderArmor EMPTY = new GenderArmor(0f, 0f, false, false, false, BreastArmorTexture.DEFAULT);

	/**
	 * Dummy implementation of {@link IGenderArmor}; simply defers to the default interface implementations for all methods.
	 */
	public static final class Default implements IGenderArmor {
		private Default() {}
	}
}
