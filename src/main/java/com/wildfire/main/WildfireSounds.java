/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.main;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class WildfireSounds {
	private static ResourceLocation femaleHurt1 = new ResourceLocation("wildfire_gender", "female_hurt1");
	public static SoundEvent FEMALE_HURT1 = new SoundEvent(femaleHurt1);
	
	private static ResourceLocation femaleHurt2 = new ResourceLocation("wildfire_gender", "female_hurt2");
	public static SoundEvent FEMALE_HURT2 = new SoundEvent(femaleHurt2);
	
	/*private static ResourceLocation maleHurt1 = new ResourceLocation("wildfire_gender", "male_hurt1");
	public static SoundEvent MALE_HURT1 = new SoundEvent(maleHurt1);*/
}
