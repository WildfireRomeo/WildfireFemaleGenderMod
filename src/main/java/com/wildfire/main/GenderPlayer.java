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

import com.google.gson.JsonObject;
import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class GenderPlayer {

	private final Map<ConfigKey<?>, Object> values = new LinkedHashMap<>();
	private final Configuration cfg;
	private final BreastPhysics lBreastPhysics, rBreastPhysics;
	public final UUID uuid;
	public boolean needsSync;
	public boolean lockSettings = false;
	public SyncStatus syncStatus = SyncStatus.UNKNOWN;

	public GenderPlayer(UUID uuid) {
		this(uuid, Configuration.GENDER.getDefault());
	}

	public GenderPlayer(UUID uuid, Gender gender) {
		lBreastPhysics = new BreastPhysics(this);
		rBreastPhysics = new BreastPhysics(this);
		this.uuid = uuid;
		this.cfg = new Configuration("WildfireGender", this.uuid.toString());
		this.cfg.set(Configuration.USERNAME, this.uuid);
		this.cfg.set(Configuration.GENDER, gender);
		addConfigDefaults(Configuration.BUST_SIZE, Configuration.HURT_SOUNDS,
		Configuration.BREASTS_OFFSET_X, Configuration.BREASTS_OFFSET_Y, Configuration.BREASTS_OFFSET_Z, Configuration.BREASTS_UNIBOOB, Configuration.BREASTS_CLEAVAGE,
		Configuration.BREAST_PHYSICS, Configuration.BREAST_PHYSICS_ARMOR, Configuration.SHOW_IN_ARMOR, Configuration.BOUNCE_MULTIPLIER, Configuration.FLOPPY_MULTIPLIER);
	}

	private void addConfigDefaults(ConfigKey<?>... keys) {
		for (ConfigKey<?> key : keys) {
			cfg.setDefault(key);
		}
		cfg.finish();
		values.put(Configuration.GENDER, cfg.get(Configuration.GENDER));
		for (ConfigKey<?> key : keys) {
			values.put(key, cfg.get(key));
		}
	}

	public Configuration getConfig() {
		return cfg;
	}

	public <TYPE> TYPE get(ConfigKey<TYPE> key) {
		if (key == Configuration.USERNAME) {
			return (TYPE) uuid;
		}
		return (TYPE) values.getOrDefault(key, key.getDefault());
	}

	public <TYPE> boolean update(ConfigKey<TYPE> key, TYPE value) {
		if (key != Configuration.USERNAME && key.validate(value) && values.containsKey(key)) {
			values.put(key, value);
			return true;
		}
		return false;
	}

	private <TYPE> void updateValue(ConfigKey<TYPE> key, Function<ConfigKey<TYPE>, TYPE> value) {
		update(key, value.apply(key));
	}

	public Gender getGender() {
		return get(Configuration.GENDER);
	}

	public float getBounceMultiplier() {
		return Math.round((get(Configuration.BOUNCE_MULTIPLIER) * 3) * 100) / 100f;
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	public static JsonObject toJsonObject(GenderPlayer plr) {
		JsonObject obj = new JsonObject();
		Configuration.USERNAME.save(obj, plr.uuid);
		for (Map.Entry<ConfigKey<?>, Object> entry : plr.values.entrySet()) {
			((ConfigKey) entry.getKey()).save(obj, entry.getValue());
		}
		return obj;
	}

	public static GenderPlayer fromJsonObject(JsonObject obj) {
		GenderPlayer plr = new GenderPlayer(Configuration.USERNAME.read(obj));
		for (Map.Entry<ConfigKey<?>, Object> entry : plr.values.entrySet()) {
			plr.updateValue(entry.getKey(), k -> k.read(obj));
		}
		return plr;
	}

	public static GenderPlayer loadCachedPlayer(UUID uuid, boolean markForSync) {
		GenderPlayer plr = WildfireGender.getPlayerById(uuid);
		if (plr != null) {
			plr.lockSettings = false;
			plr.syncStatus = SyncStatus.CACHED;
			Configuration config = plr.getConfig();
			for (Map.Entry<ConfigKey<?>, Object> entry : plr.values.entrySet()) {
				plr.updateValue(entry.getKey(), config::get);
			}
			if (markForSync) {
				plr.needsSync = true;
			}
			return plr;
		}
		return null;
	}
	
	public static void saveGenderInfo(GenderPlayer plr) {
		Configuration config = plr.getConfig();
		config.set(Configuration.USERNAME, plr.uuid);
		for (Map.Entry<ConfigKey<?>, Object> entry : plr.values.entrySet()) {
			config.set((ConfigKey) entry.getKey(), entry.getValue());
		}
		config.save();
		plr.needsSync = true;
	}

	public BreastPhysics getLeftBreastPhysics() {
		return lBreastPhysics;
	}

	public BreastPhysics getRightBreastPhysics() {
		return rBreastPhysics;
	}

	public enum SyncStatus {
		CACHED, SYNCED, UNKNOWN
	}

	public enum Gender {
		FEMALE(new TranslatableComponent("wildfire_gender.label.female").withStyle(ChatFormatting.LIGHT_PURPLE)),
		MALE(new TranslatableComponent("wildfire_gender.label.male").withStyle(ChatFormatting.BLUE)),
		OTHER(new TranslatableComponent("wildfire_gender.label.other").withStyle(ChatFormatting.GREEN));

		private final Component name;

		Gender(Component name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return name;
		}

		public boolean hasFemaleHurtSounds() {
			return this == FEMALE;
		}

		public boolean canHaveBreasts() {
			return this != MALE;
		}
	}
}
