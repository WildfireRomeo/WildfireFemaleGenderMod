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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class WildfireSounds {
	private static final ResourceLocation femaleHurt = WildfireGender.rl("female_hurt");
	public static SoundEvent FEMALE_HURT = SoundEvent.createVariableRangeEvent(femaleHurt);
	//Note: We don't register the sound event as that isn't necessary for it to play, and I believe the registry would sync
	//TODO: ^ this should be tested at some point
}
