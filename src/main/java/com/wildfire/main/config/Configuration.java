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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.wildfire.main.WildfireGender;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.util.GsonHelper;
import net.neoforged.fml.loading.FMLPaths;

public class Configuration {

	private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);

	private final File CFG_FILE;
	public JsonObject SAVE_VALUES = new JsonObject();

	public Configuration(String saveLoc, String cfgName) {
		Path saveDir = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(saveLoc));
		CFG_FILE = saveDir.resolve(cfgName + ".json").toFile();
	}

	public <TYPE> void set(ConfigKey<TYPE> key, TYPE value) {
		key.save(SAVE_VALUES, value);
	}

	public void setDefaults(ConfigKey<?>... keys) {
		for (ConfigKey<?> key : keys) {
			setDefault(key);
		}
	}

	public <TYPE> void setDefault(ConfigKey<TYPE> key) {
		if (!SAVE_VALUES.has(key.key)) {
			set(key, key.defaultValue);
		}
	}

	public <TYPE> TYPE get(ConfigKey<TYPE> key) {
		return key.read(SAVE_VALUES);
	}

	public void removeParameter(ConfigKey<?> key) {
		removeParameter(key.key);
	}
	
	public void removeParameter(String key) {
		SAVE_VALUES.remove(key);
	}
	
	public void save() {
		try (FileWriter writer = new FileWriter(CFG_FILE);
			 JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("\t");
			ADAPTER.write(jsonWriter, SAVE_VALUES);
			//WildfireGender.logger.debug("[Configuration] Saved New File!");
		} catch (IOException e) {
			WildfireGender.LOGGER.error("Failed to save Configuration", e);
		}
	}

	public void load() {
		if (!CFG_FILE.exists()) {
			return;
		}
		try (FileReader configurationFile = new FileReader(CFG_FILE)) {
			JsonObject obj = GsonHelper.parse(configurationFile);
			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				SAVE_VALUES.add(entry.getKey(), entry.getValue());
			}
		    //WildfireGender.logger.debug("[Configuration] Loaded!");
		} catch(Exception e) {
			WildfireGender.LOGGER.error("Failed to load Configuration", e);
		}
	}
}
