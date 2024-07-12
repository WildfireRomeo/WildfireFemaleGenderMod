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

package com.wildfire.main.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class GeneralClientConfig {

	public static final GeneralClientConfig INSTANCE = new GeneralClientConfig();

	public final ModConfigSpec configSpec;

	public final BooleanValue disableRendering;
	public final BooleanValue disableSoundReplacement;

	private GeneralClientConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		builder.comment("Client Config. This config only exists on the client")
			  .translation("wildfire_gender.config.client")
			  .push("client");

		disableRendering = builder.comment("Global override to disable all rendering related to the mod (including in gender menus)")
			  .translation("wildfire_gender.config.client.disable_rendering")
			  .define("disableRendering", false);
		disableSoundReplacement = builder.comment("Global override to disable replacing sounds of players with female variants")
			  .translation("wildfire_gender.config.client.disable_sound_replacement")
			  .define("disableSoundReplacement", false);

		builder.pop();
		configSpec = builder.build();
	}
}