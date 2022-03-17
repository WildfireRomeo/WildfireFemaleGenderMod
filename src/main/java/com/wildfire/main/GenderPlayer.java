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
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import java.util.UUID;

public class GenderPlayer {

	public boolean needsSync;
	public final UUID uuid;
	public Gender gender;
	public float pBustSize = Configuration.BUST_SIZE.getDefault();

	public boolean hurtSounds = Configuration.HURT_SOUNDS.getDefault();


	//physics variables
	public boolean hasBreastPhysics = Configuration.BREAST_PHYSICS.getDefault();
	public boolean hasArmorBreastPhysics = Configuration.BREAST_PHYSICS_ARMOR.getDefault();
	public float bounceMultiplier = Configuration.BOUNCE_MULTIPLIER.getDefault();
	public float floppyMultiplier = Configuration.FLOPPY_MULTIPLIER.getDefault();

	public boolean lockSettings = false;

	public SyncStatus syncStatus = SyncStatus.UNKNOWN;
	public boolean showBreastsInArmor = Configuration.SHOW_IN_ARMOR.getDefault();

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
		this.cfg.setDefault(Configuration.SHOW_ELYTRA);
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

	//send to server
	public void sendNetwork() {

	}

	//update from server
	public void getNetwork() {

	}
	public Configuration getConfig() {
		return cfg;
	}
	public float getBustSize() {
		return pBustSize;
	}

	public float getBounceMultiplier() {
		return Math.round((this.bounceMultiplier * 3) * 100) / 100f;
	}

	public float getFloppiness() {
		return this.floppyMultiplier;
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	public void updateBustSize(float v) {
		this.pBustSize = v;
	}

	public static JsonObject toJsonObject(GenderPlayer plr) {
		JsonObject obj = new JsonObject();
		Configuration.USERNAME.save(obj, plr.uuid);
		Configuration.GENDER.save(obj, plr.gender);
		Configuration.BUST_SIZE.save(obj, plr.pBustSize);
		Configuration.HURT_SOUNDS.save(obj, plr.hurtSounds);

		Configuration.BREAST_PHYSICS.save(obj, plr.hasBreastPhysics);
		Configuration.BREAST_PHYSICS_ARMOR.save(obj, plr.hasArmorBreastPhysics);
		Configuration.SHOW_IN_ARMOR.save(obj, plr.showBreastsInArmor);
		Configuration.BOUNCE_MULTIPLIER.save(obj, plr.bounceMultiplier);
		Configuration.FLOPPY_MULTIPLIER.save(obj, plr.floppyMultiplier);

		Configuration.BREASTS_OFFSET_X.save(obj, plr.getBreasts().xOffset);
		Configuration.BREASTS_OFFSET_Y.save(obj, plr.getBreasts().yOffset);
		Configuration.BREASTS_OFFSET_Z.save(obj, plr.getBreasts().zOffset);
		Configuration.BREASTS_UNIBOOB.save(obj, plr.getBreasts().isUniboob);
		Configuration.BREASTS_CLEAVAGE.save(obj, plr.getBreasts().cleavage);
		return obj;
	}

	public static GenderPlayer fromJsonObject(JsonObject obj) {
		GenderPlayer plr = new GenderPlayer(Configuration.USERNAME.read(obj));
		plr.gender = Configuration.GENDER.read(obj);
		plr.pBustSize = Configuration.BUST_SIZE.read(obj);
		plr.hurtSounds = Configuration.HURT_SOUNDS.read(obj);

		//physics
		plr.hasBreastPhysics = Configuration.BREAST_PHYSICS.read(obj);
		plr.hasArmorBreastPhysics = Configuration.BREAST_PHYSICS_ARMOR.read(obj);
		plr.showBreastsInArmor = Configuration.SHOW_IN_ARMOR.read(obj);
		plr.bounceMultiplier = Configuration.BOUNCE_MULTIPLIER.read(obj);
		plr.floppyMultiplier = Configuration.FLOPPY_MULTIPLIER.read(obj);

		plr.getBreasts().xOffset = Configuration.BREASTS_OFFSET_X.read(obj);
		plr.getBreasts().yOffset = Configuration.BREASTS_OFFSET_Y.read(obj);
		plr.getBreasts().zOffset = Configuration.BREASTS_OFFSET_Z.read(obj);
		plr.getBreasts().isUniboob = Configuration.BREASTS_UNIBOOB.read(obj);
		plr.getBreasts().cleavage = Configuration.BREASTS_CLEAVAGE.read(obj);

		return plr;
	}


	public static GenderPlayer loadCachedPlayer(UUID uuid, boolean markForSync) {
		GenderPlayer plr = WildfireGender.getPlayerById(uuid);
		if (plr != null) {
			plr.lockSettings = false;
			plr.syncStatus = SyncStatus.CACHED;
			Configuration config = plr.getConfig();
			plr.gender = config.get(Configuration.GENDER);
			plr.updateBustSize(config.get(Configuration.BUST_SIZE));
			plr.hurtSounds = config.get(Configuration.HURT_SOUNDS);

			//physics
			plr.hasBreastPhysics = config.get(Configuration.BREAST_PHYSICS);
			plr.hasArmorBreastPhysics = config.get(Configuration.BREAST_PHYSICS_ARMOR);
			plr.showBreastsInArmor = config.get(Configuration.SHOW_IN_ARMOR);
			plr.bounceMultiplier = config.get(Configuration.BOUNCE_MULTIPLIER);
			plr.floppyMultiplier = config.get(Configuration.FLOPPY_MULTIPLIER);

			plr.getBreasts().xOffset = config.get(Configuration.BREASTS_OFFSET_X);
			plr.getBreasts().yOffset = config.get(Configuration.BREASTS_OFFSET_Y);
			plr.getBreasts().zOffset = config.get(Configuration.BREASTS_OFFSET_Z);
			plr.getBreasts().isUniboob = config.get(Configuration.BREASTS_UNIBOOB);
			plr.getBreasts().cleavage = config.get(Configuration.BREASTS_CLEAVAGE);
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
		config.set(Configuration.GENDER, plr.gender);
		config.set(Configuration.BUST_SIZE, plr.getBustSize());
		config.set(Configuration.HURT_SOUNDS, plr.hurtSounds);

		//physics
		config.set(Configuration.BREAST_PHYSICS, plr.hasBreastPhysics);
		config.set(Configuration.BREAST_PHYSICS_ARMOR, plr.hasArmorBreastPhysics);
		config.set(Configuration.SHOW_IN_ARMOR, plr.showBreastsInArmor);
		config.set(Configuration.BOUNCE_MULTIPLIER, plr.bounceMultiplier);
		config.set(Configuration.FLOPPY_MULTIPLIER, plr.floppyMultiplier);

		config.set(Configuration.BREASTS_OFFSET_X, plr.getBreasts().xOffset);
		config.set(Configuration.BREASTS_OFFSET_Y, plr.getBreasts().yOffset);
		config.set(Configuration.BREASTS_OFFSET_Z, plr.getBreasts().zOffset);
		config.set(Configuration.BREASTS_UNIBOOB, plr.getBreasts().isUniboob);
		config.set(Configuration.BREASTS_CLEAVAGE, plr.getBreasts().cleavage);

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
		FEMALE(new TranslatableText("wildfire_gender.label.female").formatted(Formatting.LIGHT_PURPLE)),
		MALE(new TranslatableText("wildfire_gender.label.male").formatted(Formatting.BLUE)),
		OTHER(new TranslatableText("wildfire_gender.label.other").formatted(Formatting.GREEN));

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
