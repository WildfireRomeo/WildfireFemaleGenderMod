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
import org.jetbrains.annotations.NotNull;
import com.wildfire.api.Vec2i;

/**
 * Default record implementation of {@link IBreastArmorTexture} used for resource pack entries
 *
 * @see IBreastArmorTexture
 */
public record BreastArmorTexture(@NotNull Vec2i textureSize, @NotNull Vec2i leftUv, @NotNull Vec2i rightUv, @NotNull Vec2i dimensions) implements IBreastArmorTexture {
	/**
	 * Default breast texture values, intended for armors compatible with the vanilla armor renderer.
	 */
	public static final IBreastArmorTexture DEFAULT = new Default();

	/**
	 * Dummy implementation of {@link IBreastArmorTexture}; simply defers to the default interface implementations for all methods.
	 */
	public static final class Default implements IBreastArmorTexture {
		private Default() {}
	}
}
