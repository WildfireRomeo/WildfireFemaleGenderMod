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
import com.wildfire.api.impl.BreastArmorTexture;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the texture data for a given armor piece when covering an entity's breasts
 */
public interface IBreastArmorTexture {

	Vec2i DEFAULT_TEXTURE_SIZE = new Vec2i(64, 32);
	Vec2i DEFAULT_DIMENSIONS = new Vec2i(4, 5);
	Vec2i DEFAULT_LEFT_UV = new Vec2i(16, 17);
	Vec2i DEFAULT_RIGHT_UV = DEFAULT_LEFT_UV.add(DEFAULT_DIMENSIONS.x(), 0);

	Codec<IBreastArmorTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Vec2i.CODEC
					.optionalFieldOf("texture_size", BreastArmorTexture.DEFAULT.textureSize())
					.forGetter(IBreastArmorTexture::textureSize),
			Vec2i.CODEC
					.optionalFieldOf("left_uv", BreastArmorTexture.DEFAULT.leftUv())
					.forGetter(IBreastArmorTexture::leftUv),
			Vec2i.CODEC
					.optionalFieldOf("right_uv", new Vec2i(-1, -1))
					.forGetter(IBreastArmorTexture::rightUv),
			Vec2i.CODEC
					.optionalFieldOf("dimensions", BreastArmorTexture.DEFAULT.dimensions())
					.forGetter(IBreastArmorTexture::dimensions)
	).apply(instance, (size, leftUv, rightUv, dimensions) -> {
		var right = rightUv;
		if(right.x() == -1 && right.y() == -1) {
			right = leftUv.add(dimensions.x(), 0);
		}
		return new BreastArmorTexture(size, leftUv, right, dimensions);
	}));

	/**
	 * The size of the armor sprite in pixels
	 *
	 * @implNote Defaults to {@code Vec2i(64, 32)}
	 *
	 * @return A {@link Vec2i} indicating how large the texture file is
	 */
	default @NotNull Vec2i textureSize() {
		return DEFAULT_TEXTURE_SIZE;
	}

	/**
	 * How large of an area from the sprite should be used for each breast
	 *
	 * @apiNote The X value of this should be halved from the total chest size to account for each breast side
	 *          rendering independently of each other.
	 *
	 * @implNote Defaults to {@code Vec2i(4, 5)}
	 *
	 * @return A {@link Vec2i} indicating how large of an area should be grabbed from the texture sprite to display over
	 *         the wearer's breasts
	 */
	default @NotNull Vec2i dimensions() {
		return DEFAULT_DIMENSIONS;
	}

	/**
	 * Where the left breast should grab the texture from on the sprite
	 *
	 * @implNote Defaults to {@code Vec2i(16, 17)}
	 *
	 * @return A {@link Vec2i} indicating the UV to use for the left breast
	 */
	default @NotNull Vec2i leftUv() {
		return DEFAULT_LEFT_UV;
	}

	/**
	 * Where the right breast should grab the texture from on the sprite
	 *
	 * @implNote Defaults to {@code Vec2i(20, 17)}
	 *
	 * @return A {@link Vec2i} indicating the UV to use for the right breast
	 */
	default @NotNull Vec2i rightUv() {
		return DEFAULT_RIGHT_UV;
	}
}
