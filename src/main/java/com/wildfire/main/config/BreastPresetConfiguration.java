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
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class BreastPresetConfiguration {

	public static final StringConfigKey PRESET_NAME = new StringConfigKey("preset_name", "");
	public static final FloatConfigKey BUST_SIZE = new FloatConfigKey("bust_size", 0.6F, 0, 1);

	public static final FloatConfigKey BREASTS_OFFSET_X = new FloatConfigKey("breasts_xOffset", 0.0F, -1, 1);
	public static final FloatConfigKey BREASTS_OFFSET_Y = new FloatConfigKey("breasts_yOffset", 0.0F, -1, 1);
	public static final FloatConfigKey BREASTS_OFFSET_Z = new FloatConfigKey("breasts_zOffset", 0.0F, -1, 0);
	public static final BooleanConfigKey BREASTS_UNIBOOB = new BooleanConfigKey("breasts_uniboob", true);
	public static final FloatConfigKey BREASTS_CLEAVAGE = new FloatConfigKey("breasts_cleavage", 0, 0, 0.1F);

	private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);

	private final File CFG_FILE;
	public JsonObject SAVE_VALUES = new JsonObject();

	public BreastPresetConfiguration(String cfgName) {

		Path saveDir = FabricLoader.getInstance().getConfigDir();
		System.out.println("Breast Presets Save Dir: " + saveDir.toString());
		if(!Files.isDirectory(saveDir.resolve("WildfireGender/presets"))) {
			try {
				Files.createDirectory(saveDir.resolve("WildfireGender/presets"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(saveLoc), saveLoc);
		CFG_FILE = saveDir.resolve("WildfireGender/presets").resolve(cfgName + ".json").toFile();
	}

	public static BreastPresetConfiguration[] getBreastPresetConfigurationFiles() {
		ArrayList<BreastPresetConfiguration> tmp = new ArrayList<>();

		Path saveDir = FabricLoader.getInstance().getConfigDir();
		File presetFileLocation = saveDir.resolve("WildfireGender/presets").toFile();
		if(!presetFileLocation.exists()) {
			presetFileLocation.mkdirs();
		}
		File[] presetFiles = presetFileLocation.listFiles();
		if(presetFiles.length > 0) {
			for (File f : presetFiles) {
				BreastPresetConfiguration cfg = new BreastPresetConfiguration(f.getName().replace(".json", ""));
				cfg.load(); // Load the preset values
				tmp.add(cfg);
			}
		}

		if(tmp.size() == 0) {
			return new BreastPresetConfiguration[] {};
		}
		return tmp.toArray(new BreastPresetConfiguration[tmp.size()]);
	}

	public void finish() {
		if(CFG_FILE.exists()) {
			load(); //load file
			updateConfig();
		} else {
			//save(); //save all values to default in new file.
		}
	}

	public <TYPE> void set(ConfigKey<TYPE> key, TYPE value) {
		key.save(SAVE_VALUES, value);
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
	
	public void updateConfig() {
		JsonObject obj;
		try (FileReader configurationFile = new FileReader(CFG_FILE)) {
			obj = new Gson().fromJson(configurationFile, JsonObject.class); //GsonHelper.parse(configurationFile);
			//Merge with existing values
			for (Map.Entry<String, JsonElement> entry : SAVE_VALUES.entrySet()) {
				obj.add(entry.getKey(), entry.getValue());
			}
		} catch(Exception ignored) {
			return;
		}
		try (FileWriter writer = new FileWriter(CFG_FILE);
			 JsonWriter jsonWriter = new JsonWriter(writer)) {
			ADAPTER.write(jsonWriter, obj);
			//System.out.println("[Configuration] Saved Existing File!");
		} catch(Exception ignored) {}
	}
	
	public void save() {
		try (FileWriter writer = new FileWriter(CFG_FILE);
			 JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("		");
			ADAPTER.write(jsonWriter, SAVE_VALUES);
			//System.out.println("[Configuration] Saved New File!");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	//load file values to this class for use in the program
	public void load() {
		 //System.out.println("[Configuration] Loading...");
		
		try (FileReader configurationFile = new FileReader(CFG_FILE)) {
			JsonObject obj = new Gson().fromJson(configurationFile, JsonObject.class);
			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				String key = entry.getKey();
				SAVE_VALUES.add(key, entry.getValue());
			}
		    //System.out.println("[Configuration] Loaded!\n\n");
		} catch(Exception e) {
		    //System.out.println("[Configuration] Failed!\n\n");
		    e.printStackTrace();
		}
	}
}
