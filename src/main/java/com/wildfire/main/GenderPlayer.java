/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022 WildfireRomeo

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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;
import java.util.function.Consumer;

public class GenderPlayer {

	public boolean needsSync;
	public final UUID uuid;
	private Gender gender;
	private float pBustSize = Configuration.BUST_SIZE.getDefault();

	private boolean hurtSounds = Configuration.HURT_SOUNDS.getDefault();

	//physics variables
	private boolean breastPhysics = Configuration.BREAST_PHYSICS.getDefault();
	private boolean armorBreastPhysics = Configuration.BREAST_PHYSICS_ARMOR.getDefault();
	private float bounceMultiplier = Configuration.BOUNCE_MULTIPLIER.getDefault();
	private float floppyMultiplier = Configuration.FLOPPY_MULTIPLIER.getDefault();

	public boolean lockSettings = false;

	public SyncStatus syncStatus = SyncStatus.UNKNOWN;
	private boolean showBreastsInArmor = Configuration.SHOW_IN_ARMOR.getDefault();

	private final Configuration cfg;
	private final BreastPhysics lBreastPhysics, rBreastPhysics;
	private final Breasts breasts;

	public GenderPlayer(UUID uuid) {
		this(uuid, Configuration.GENDER.getDefault());
	}

	public GenderPlayer(UUID uuid, Gender gender) {
		lBreastPhysics = new BreastPhysics(this);
		rBreastPhysics = new BreastPhysics(this);
		breasts = new Breasts();
		this.uuid = uuid;
		this.gender = gender;
		this.cfg = new Configuration("WildfireGender", this.uuid.toString());
		this.cfg.set(Configuration.USERNAME, this.uuid);
		this.cfg.setDefault(Configuration.GENDER);
		this.cfg.setDefault(Configuration.BUST_SIZE);
		this.cfg.setDefault(Configuration.HURT_SOUNDS);

		this.cfg.setDefault(Configuration.BREASTS_OFFSET_X);
		this.cfg.setDefault(Configuration.BREASTS_OFFSET_Y);
		this.cfg.setDefault(Configuration.BREASTS_OFFSET_Z);
		this.cfg.setDefault(Configuration.BREASTS_UNIBOOB);
		this.cfg.setDefault(Configuration.BREASTS_CLEAVAGE);

		this.cfg.setDefault(Configuration.BREAST_PHYSICS);
		this.cfg.setDefault(Configuration.BREAST_PHYSICS_ARMOR);
		this.cfg.setDefault(Configuration.SHOW_IN_ARMOR);
		this.cfg.setDefault(Configuration.BOUNCE_MULTIPLIER);
		this.cfg.setDefault(Configuration.FLOPPY_MULTIPLIER);
		this.cfg.finish();
	}

	public Configuration getConfig() {
		return cfg;
	}

	private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
		if (key.validate(value)) {
			setter.accept(value);
			return true;
		}
		return false;
	}

	public Gender getGender() {
		return gender;
	}

	public boolean updateGender(Gender value) {
		return updateValue(Configuration.GENDER, value, v -> this.gender = v);
	}

	public float getBustSize() {
		return pBustSize;
	}

	public boolean updateBustSize(float value) {
		return updateValue(Configuration.BUST_SIZE, value, v -> this.pBustSize = v);
	}

	public boolean hasHurtSounds() {
		return hurtSounds;
	}

	public boolean updateHurtSounds(boolean value) {
		return updateValue(Configuration.HURT_SOUNDS, value, v -> this.hurtSounds = v);
	}

	public boolean hasBreastPhysics() {
		return breastPhysics;
	}

	public boolean updateBreastPhysics(boolean value) {
		return updateValue(Configuration.BREAST_PHYSICS, value, v -> this.breastPhysics = v);
	}

	public boolean hasArmorBreastPhysics() {
		return armorBreastPhysics;
	}

	public boolean updateArmorBreastPhysics(boolean value) {
		return updateValue(Configuration.BREAST_PHYSICS_ARMOR, value, v -> this.armorBreastPhysics = v);
	}

	public boolean showBreastsInArmor() {
		return showBreastsInArmor;
	}

	public boolean updateShowBreastsInArmor(boolean value) {
		return updateValue(Configuration.SHOW_IN_ARMOR, value, v -> this.showBreastsInArmor = v);
	}

	public float getBounceMultiplier() {
		return Math.round((this.getBounceMultiplierRaw() * 3) * 100) / 100f;
	}

	public float getBounceMultiplierRaw() {
		return bounceMultiplier;
	}

	public boolean updateBounceMultiplier(float value) {
		return updateValue(Configuration.BOUNCE_MULTIPLIER, value, v -> this.bounceMultiplier = v);
	}

	public float getFloppiness() {
		return this.floppyMultiplier;
	}

	public boolean updateFloppiness(float value) {
		return updateValue(Configuration.FLOPPY_MULTIPLIER, value, v -> this.floppyMultiplier = v);
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	public static JsonObject toJsonObject(GenderPlayer plr) {
		JsonObject obj = new JsonObject();
		Configuration.USERNAME.save(obj, plr.uuid);
		Configuration.GENDER.save(obj, plr.getGender());
		Configuration.BUST_SIZE.save(obj, plr.getBustSize());
		Configuration.HURT_SOUNDS.save(obj, plr.hasHurtSounds());

		Configuration.BREAST_PHYSICS.save(obj, plr.hasBreastPhysics());
		Configuration.BREAST_PHYSICS_ARMOR.save(obj, plr.hasArmorBreastPhysics());
		Configuration.SHOW_IN_ARMOR.save(obj, plr.showBreastsInArmor());
		Configuration.BOUNCE_MULTIPLIER.save(obj, plr.getBounceMultiplierRaw());
		Configuration.FLOPPY_MULTIPLIER.save(obj, plr.getFloppiness());

		Breasts breasts = plr.getBreasts();
		Configuration.BREASTS_OFFSET_X.save(obj, breasts.getXOffset());
		Configuration.BREASTS_OFFSET_Y.save(obj, breasts.getYOffset());
		Configuration.BREASTS_OFFSET_Z.save(obj, breasts.getZOffset());
		Configuration.BREASTS_UNIBOOB.save(obj, breasts.isUniboob());
		Configuration.BREASTS_CLEAVAGE.save(obj, breasts.getCleavage());
		return obj;
	}

	public static GenderPlayer fromJsonObject(JsonObject obj) {
		GenderPlayer plr = new GenderPlayer(Configuration.USERNAME.read(obj));
		plr.updateGender(Configuration.GENDER.read(obj));
		plr.updateBustSize(Configuration.BUST_SIZE.read(obj));
		plr.updateHurtSounds(Configuration.HURT_SOUNDS.read(obj));

		//physics
		plr.updateBreastPhysics(Configuration.BREAST_PHYSICS.read(obj));
		plr.updateArmorBreastPhysics(Configuration.BREAST_PHYSICS_ARMOR.read(obj));
		plr.updateShowBreastsInArmor(Configuration.SHOW_IN_ARMOR.read(obj));
		plr.updateBounceMultiplier(Configuration.BOUNCE_MULTIPLIER.read(obj));
		plr.updateFloppiness(Configuration.FLOPPY_MULTIPLIER.read(obj));

		Breasts breasts = plr.getBreasts();
		breasts.updateXOffset(Configuration.BREASTS_OFFSET_X.read(obj));
		breasts.updateYOffset(Configuration.BREASTS_OFFSET_Y.read(obj));
		breasts.updateZOffset(Configuration.BREASTS_OFFSET_Z.read(obj));
		breasts.updateUniboob(Configuration.BREASTS_UNIBOOB.read(obj));
		breasts.updateCleavage(Configuration.BREASTS_CLEAVAGE.read(obj));

		return plr;
	}


	public static GenderPlayer loadCachedPlayer(UUID uuid, boolean markForSync) {
		GenderPlayer plr = WildfireGender.getPlayerById(uuid);
		if (plr != null) {
			plr.lockSettings = false;
			plr.syncStatus = SyncStatus.CACHED;
			Configuration config = plr.getConfig();
			plr.updateGender(config.get(Configuration.GENDER));
			plr.updateBustSize(config.get(Configuration.BUST_SIZE));
			plr.updateHurtSounds(config.get(Configuration.HURT_SOUNDS));

			//physics
			plr.updateBreastPhysics(config.get(Configuration.BREAST_PHYSICS));
			plr.updateArmorBreastPhysics(config.get(Configuration.BREAST_PHYSICS_ARMOR));
			plr.updateShowBreastsInArmor(config.get(Configuration.SHOW_IN_ARMOR));
			plr.updateBounceMultiplier(config.get(Configuration.BOUNCE_MULTIPLIER));
			plr.updateFloppiness(config.get(Configuration.FLOPPY_MULTIPLIER));

			Breasts breasts = plr.getBreasts();
			breasts.updateXOffset(config.get(Configuration.BREASTS_OFFSET_X));
			breasts.updateYOffset(config.get(Configuration.BREASTS_OFFSET_Y));
			breasts.updateZOffset(config.get(Configuration.BREASTS_OFFSET_Z));
			breasts.updateUniboob(config.get(Configuration.BREASTS_UNIBOOB));
			breasts.updateCleavage(config.get(Configuration.BREASTS_CLEAVAGE));
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
		config.set(Configuration.GENDER, plr.getGender());
		config.set(Configuration.BUST_SIZE, plr.getBustSize());
		config.set(Configuration.HURT_SOUNDS, plr.hasHurtSounds());

		//physics
		config.set(Configuration.BREAST_PHYSICS, plr.hasBreastPhysics());
		config.set(Configuration.BREAST_PHYSICS_ARMOR, plr.hasArmorBreastPhysics());
		config.set(Configuration.SHOW_IN_ARMOR, plr.showBreastsInArmor());
		config.set(Configuration.BOUNCE_MULTIPLIER, plr.getBounceMultiplierRaw());
		config.set(Configuration.FLOPPY_MULTIPLIER, plr.getFloppiness());

		config.set(Configuration.BREASTS_OFFSET_X, plr.getBreasts().getXOffset());
		config.set(Configuration.BREASTS_OFFSET_Y, plr.getBreasts().getYOffset());
		config.set(Configuration.BREASTS_OFFSET_Z, plr.getBreasts().getZOffset());
		config.set(Configuration.BREASTS_UNIBOOB, plr.getBreasts().isUniboob());
		config.set(Configuration.BREASTS_CLEAVAGE, plr.getBreasts().getCleavage());

		config.save();
		plr.needsSync = true;
	}

	public Breasts getBreasts() {
		return breasts;
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
		FEMALE(Text.translatable("wildfire_gender.label.female").formatted(Formatting.LIGHT_PURPLE)),
		MALE(Text.translatable("wildfire_gender.label.male").formatted(Formatting.BLUE)),
		OTHER(Text.translatable("wildfire_gender.label.other").formatted(Formatting.GREEN));

		private final Text name;

		Gender(Text name) {
			this.name = name;
		}

		public Text getDisplayName() {
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
