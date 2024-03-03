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
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractConfiguration {

	public static final boolean SUPPORTS_SAVING = FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER;
	private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);

	protected final File configFile;
	public final JsonObject values = new JsonObject();

	protected AbstractConfiguration(String... path) {
		configFile = resolve(path).toFile();
		Path dir = configFile.getParentFile().toPath();
		createDir(dir);
	}

	protected AbstractConfiguration(Path dir, String name) {
		configFile = dir.resolve(name).toFile();
		createDir(dir);
	}

	protected static Path resolve(String... path) {
		Path resolved = FabricLoader.getInstance().getConfigDir();
		for(String name : path) {
			resolved = resolved.resolve(name);
		}
		return resolved;
	}

	private static void createDir(Path dir) {
		if(!SUPPORTS_SAVING) return;
		if(!Files.exists(dir)) {
			try {
				Files.createDirectories(dir);
			} catch(IOException e) {
				WildfireGender.LOGGER.error("Failed to create config directory", e);
			}
		}
	}

	public <TYPE> void set(ConfigKey<TYPE> key, TYPE value) {
		key.save(values, value);
	}

	public <TYPE> TYPE get(ConfigKey<TYPE> key) {
		return key.read(values);
	}

	public <TYPE> void setDefault(ConfigKey<TYPE> key) {
		if(!values.has(key.key)) {
			set(key, key.defaultValue);
		}
	}

	public void setDefaults() {
		Arrays.stream(this.getClass().getFields())
				.filter(field -> ConfigKey.class.isAssignableFrom(field.getType()))
				.filter(field -> Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))
				.map(field -> {
					try {
						return (ConfigKey<?>) field.get(null);
					} catch(ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				})
				.forEach(this::setDefault);
	}

	public void removeParameter(ConfigKey<?> key) {
		removeParameter(key.key);
	}

	public void removeParameter(String key) {
		values.remove(key);
	}

	public void save() {
		if(!SUPPORTS_SAVING) return;
		try(FileWriter writer = new FileWriter(configFile); JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("\t");
			ADAPTER.write(jsonWriter, values);
		} catch(IOException e) {
			WildfireGender.LOGGER.error("Failed to save config file", e);
		}
	}

	public void load() {
		if(!SUPPORTS_SAVING) return;
		if(!configFile.exists()) return;
		try(FileReader configurationFile = new FileReader(configFile)) {
			JsonObject obj = new Gson().fromJson(configurationFile, JsonObject.class);
			for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				values.add(entry.getKey(), entry.getValue());
			}
		} catch(IOException e) {
			WildfireGender.LOGGER.error("Failed to load config file", e);
		}
	}
}
