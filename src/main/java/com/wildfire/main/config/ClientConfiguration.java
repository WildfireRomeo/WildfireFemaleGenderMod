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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientConfiguration extends AbstractConfiguration {

	public static final ClientConfiguration INSTANCE;

	@SuppressWarnings("unused")
	public static final StringConfigKey COMMENT = new StringConfigKey("__note", "Any changes made to this file will only take effect once the game is restarted");

	public static final BooleanConfigKey ARMOR_PHYSICS_OVERRIDE = new BooleanConfigKey("armor_physics_override", false);
	public static final BooleanConfigKey ENABLE_BREAST_RENDERING = new BooleanConfigKey("enable_breast_rendering", true);
	public static final BooleanConfigKey ENABLE_GENDER_HURT_SOUNDS = new BooleanConfigKey("enable_gender_hurt_sounds", true);

	private ClientConfiguration(String... path) {
		super(path);
	}

	static {
		INSTANCE = new ClientConfiguration(Configuration.CONFIG_DIR, "client.json");
		INSTANCE.setDefaults(COMMENT, ARMOR_PHYSICS_OVERRIDE, ENABLE_BREAST_RENDERING, ENABLE_GENDER_HURT_SOUNDS);
		if(INSTANCE.configFile.exists()) {
			INSTANCE.load();
		} else {
			INSTANCE.save();
		}
	}
}
