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

package com.wildfire.main;

import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public enum Gender {

	// NOTE: The order of these should remain unchanged! Changing these WILL modify player configs!
	FEMALE(Text.translatable("wildfire_gender.label.female").formatted(Formatting.LIGHT_PURPLE), true, WildfireSounds.FEMALE_HURT),
	MALE(Text.translatable("wildfire_gender.label.male").formatted(Formatting.BLUE), false, null),
	OTHER(Text.translatable("wildfire_gender.label.other").formatted(Formatting.GREEN), true, WildfireSounds.FEMALE_HURT);

	private final Text name;
	private final boolean canHaveBreasts;
	private final @Nullable SoundEvent hurtSound;

	Gender(Text name, boolean canHaveBreasts, @Nullable SoundEvent hurtSound) {
		this.name = name;
		this.canHaveBreasts = canHaveBreasts;
		this.hurtSound = hurtSound;
	}

	public Text getDisplayName() {
		return name;
	}

	public @Nullable SoundEvent getHurtSound() {
		return hurtSound;
	}

	public boolean canHaveBreasts() {
		return canHaveBreasts;
	}
}
