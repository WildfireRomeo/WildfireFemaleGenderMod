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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Simplified immutable copy of a {@link org.joml.Vector2i Vector2i}
 */
public record Vec2i(int x, int y) {
	// TODO a [x, y] list would be preferred but I don't understand codecs enough to try to get that to work
	public static final Codec<Vec2i> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("x").forGetter(Vec2i::x),
			Codec.INT.fieldOf("y").forGetter(Vec2i::y)
	).apply(instance, Vec2i::new));

	/**
	 * Returns a new {@link Vec2i} with the sum of the provided values and this {@link Vec2i}
	 *
	 * @param x The value to add to this vector's X value
	 * @param y The value to add to this vector's Y value
	 *
	 * @return A new {@link Vec2i} with the added values
	 */
	public Vec2i add(int x, int y) {
		return new Vec2i(this.x + x, this.y + y);
	}
}
