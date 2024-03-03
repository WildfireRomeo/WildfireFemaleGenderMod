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

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class BreastPresetConfiguration extends AbstractConfiguration {

	private static final Path PRESETS_DIR = resolve(Configuration.CONFIG_DIR, "presets");

	public static final StringConfigKey PRESET_NAME = new StringConfigKey("preset_name", "");
	public static final FloatConfigKey BUST_SIZE = new FloatConfigKey("bust_size", 0.6F, 0, 0.8f);

	public static final FloatConfigKey BREASTS_OFFSET_X = new FloatConfigKey("breasts_xOffset", 0.0F, -1, 1);
	public static final FloatConfigKey BREASTS_OFFSET_Y = new FloatConfigKey("breasts_yOffset", 0.0F, -1, 1);
	public static final FloatConfigKey BREASTS_OFFSET_Z = new FloatConfigKey("breasts_zOffset", 0.0F, -1, 0);
	public static final BooleanConfigKey BREASTS_UNIBOOB = new BooleanConfigKey("breasts_uniboob", true);
	public static final FloatConfigKey BREASTS_CLEAVAGE = new FloatConfigKey("breasts_cleavage", 0, 0, 0.1F);

	public BreastPresetConfiguration(String cfgName) {
		super(PRESETS_DIR, cfgName + ".json");
	}

	public static BreastPresetConfiguration[] getBreastPresetConfigurationFiles() {
		ArrayList<BreastPresetConfiguration> presets = new ArrayList<>();
		File saveDir = FabricLoader.getInstance().getConfigDir().resolve(PRESETS_DIR).toFile();

		if(!saveDir.exists()) {
			saveDir.mkdirs();
		}
		File[] presetFiles = saveDir.listFiles();
		if(presetFiles != null) {
			for(File f : presetFiles) {
				// strip the trailing '.json'
				String name = f.getName().substring(0, f.getName().length() - 5);
				BreastPresetConfiguration cfg = new BreastPresetConfiguration(name);
				cfg.load(); // load from file
				presets.add(cfg);
			}
		}

		return presets.toArray(BreastPresetConfiguration[]::new);
	}
}
