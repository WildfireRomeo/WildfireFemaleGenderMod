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

package com.wildfire.main;

import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class GenderPlayer {

	public boolean needsSync;
	public final UUID uuid;
	private Gender gender;
	private float pBustSize = ClientConfiguration.BUST_SIZE.getDefault();

	private boolean hurtSounds = ClientConfiguration.HURT_SOUNDS.getDefault();

	//physics variables
	private boolean breastPhysics = ClientConfiguration.BREAST_PHYSICS.getDefault();
	private float bounceMultiplier = ClientConfiguration.BOUNCE_MULTIPLIER.getDefault();
	private float floppyMultiplier = ClientConfiguration.FLOPPY_MULTIPLIER.getDefault();

	public SyncStatus syncStatus = SyncStatus.UNKNOWN;
	private boolean showBreastsInArmor = ClientConfiguration.SHOW_IN_ARMOR.getDefault();
	private boolean armorPhysOverride = ClientConfiguration.ARMOR_PHYSICS_OVERRIDE.getDefault();

	private final ClientConfiguration cfg;
	private final BreastPhysics lBreastPhysics, rBreastPhysics;
	private final Breasts breasts;

	public GenderPlayer(UUID uuid) {
		this(uuid, ClientConfiguration.GENDER.getDefault());
	}

	public GenderPlayer(UUID uuid, Gender gender) {
		lBreastPhysics = new BreastPhysics(this);
		rBreastPhysics = new BreastPhysics(this);
		breasts = new Breasts();
		this.uuid = uuid;
		this.gender = gender;
		this.cfg = new ClientConfiguration("WildfireGender", this.uuid.toString());
		this.cfg.set(ClientConfiguration.USERNAME, this.uuid);
		this.cfg.setDefaults(
			ClientConfiguration.GENDER,
			ClientConfiguration.BUST_SIZE,
			ClientConfiguration.HURT_SOUNDS,

			ClientConfiguration.BREASTS_OFFSET_X,
			ClientConfiguration.BREASTS_OFFSET_Y,
			ClientConfiguration.BREASTS_OFFSET_Z,
			ClientConfiguration.BREASTS_UNIBOOB,
			ClientConfiguration.BREASTS_CLEAVAGE,

			ClientConfiguration.BREAST_PHYSICS,
			ClientConfiguration.ARMOR_PHYSICS_OVERRIDE,
			ClientConfiguration.SHOW_IN_ARMOR,
			ClientConfiguration.BOUNCE_MULTIPLIER,
			ClientConfiguration.FLOPPY_MULTIPLIER
		);
		this.cfg.finish();
	}

	public ClientConfiguration getConfig() {
		return cfg;
	}

	private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
		if (key.validate(value)) {
			setter.accept(value);
			return true;
		}
		return false;
	}

	public <VALUE> boolean updateFrom(ConfigKey<VALUE> key, Configuration copyFrom, Consumer<VALUE> setter) {
		VALUE value = copyFrom.get(key);
		if (value == null) {
			return false;
		}
		return updateValue(key, value, setter);
	}

	public Gender getGender() {
		return gender;
	}

	public boolean updateGender(Gender value) {
		return updateValue(ClientConfiguration.GENDER, value, v -> this.gender = v);
	}

	public float getBustSize() {
		return pBustSize;
	}

	public boolean updateBustSize(float value) {
		return updateValue(ClientConfiguration.BUST_SIZE, value, v -> this.pBustSize = v);
	}

	public boolean updateBustSize(Configuration copyFrom) {
		return updateFrom(ClientConfiguration.BUST_SIZE, copyFrom, v -> this.pBustSize = v);
	}

	public boolean hasHurtSounds() {
		return hurtSounds;
	}

	public boolean updateHurtSounds(boolean value) {
		return updateValue(ClientConfiguration.HURT_SOUNDS, value, v -> this.hurtSounds = v);
	}

	public boolean hasBreastPhysics() {
		return breastPhysics;
	}

	public boolean updateBreastPhysics(boolean value) {
		return updateValue(ClientConfiguration.BREAST_PHYSICS, value, v -> this.breastPhysics = v);
	}

	public boolean getArmorPhysicsOverride() {
		return armorPhysOverride;
	}

	public boolean updateArmorPhysicsOverride(boolean value) {
		return updateValue(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, value, v -> this.armorPhysOverride = v);
	}

	public boolean showBreastsInArmor() {
		return showBreastsInArmor;
	}

	public boolean updateShowBreastsInArmor(boolean value) {
		return updateValue(ClientConfiguration.SHOW_IN_ARMOR, value, v -> this.showBreastsInArmor = v);
	}

	public float getBounceMultiplier() {
		return Math.round((this.getBounceMultiplierRaw() * 3) * 100) / 100f;
	}

	public float getBounceMultiplierRaw() {
		return bounceMultiplier;
	}

	public boolean updateBounceMultiplier(float value) {
		return updateValue(ClientConfiguration.BOUNCE_MULTIPLIER, value, v -> this.bounceMultiplier = v);
	}

	public float getFloppiness() {
		return this.floppyMultiplier;
	}

	public boolean updateFloppiness(float value) {
		return updateValue(ClientConfiguration.FLOPPY_MULTIPLIER, value, v -> this.floppyMultiplier = v);
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	public static GenderPlayer loadCachedPlayer(UUID uuid, boolean markForSync) {
		GenderPlayer plr = WildfireGender.getPlayerById(uuid);
		if (plr != null) {
			plr.syncStatus = SyncStatus.CACHED;
			ClientConfiguration config = plr.getConfig();
			plr.updateGender(config.get(ClientConfiguration.GENDER));
			plr.updateBustSize(config);
			plr.updateHurtSounds(config.get(ClientConfiguration.HURT_SOUNDS));

			//physics
			plr.updateBreastPhysics(config.get(ClientConfiguration.BREAST_PHYSICS));
			plr.updateShowBreastsInArmor(config.get(ClientConfiguration.SHOW_IN_ARMOR));
			plr.updateArmorPhysicsOverride(config.get(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE));
			plr.updateBounceMultiplier(config.get(ClientConfiguration.BOUNCE_MULTIPLIER));
			plr.updateFloppiness(config.get(ClientConfiguration.FLOPPY_MULTIPLIER));

			plr.getBreasts().copyFrom(config);
			if (markForSync) {
				plr.needsSync = true;
			}
			return plr;
		}
		return null;
	}
	
	public static void saveGenderInfo(GenderPlayer plr) {
		ClientConfiguration config = plr.getConfig();
		config.set(ClientConfiguration.USERNAME, plr.uuid);
		config.set(ClientConfiguration.GENDER, plr.getGender());
		config.set(ClientConfiguration.BUST_SIZE, plr.getBustSize());
		config.set(ClientConfiguration.HURT_SOUNDS, plr.hasHurtSounds());

		//physics
		config.set(ClientConfiguration.BREAST_PHYSICS, plr.hasBreastPhysics());
		config.set(ClientConfiguration.SHOW_IN_ARMOR, plr.showBreastsInArmor());
		config.set(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, plr.getArmorPhysicsOverride());
		config.set(ClientConfiguration.BOUNCE_MULTIPLIER, plr.getBounceMultiplierRaw());
		config.set(ClientConfiguration.FLOPPY_MULTIPLIER, plr.getFloppiness());

		config.set(ClientConfiguration.BREASTS_OFFSET_X, plr.getBreasts().getXOffset());
		config.set(ClientConfiguration.BREASTS_OFFSET_Y, plr.getBreasts().getYOffset());
		config.set(ClientConfiguration.BREASTS_OFFSET_Z, plr.getBreasts().getZOffset());
		config.set(ClientConfiguration.BREASTS_UNIBOOB, plr.getBreasts().isUniboob());
		config.set(ClientConfiguration.BREASTS_CLEAVAGE, plr.getBreasts().getCleavage());

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
		FEMALE(Component.translatable("wildfire_gender.label.female").withStyle(ChatFormatting.LIGHT_PURPLE), true, WildfireSounds.FEMALE_HURT),
		MALE(Component.translatable("wildfire_gender.label.male").withStyle(ChatFormatting.BLUE), false, null),
		OTHER(Component.translatable("wildfire_gender.label.other").withStyle(ChatFormatting.GREEN), true, null);

		private final Component name;
		@Nullable
		private final SoundEvent hurtSound;
		private final boolean canHaveBreasts;

		Gender(Component name, boolean canHaveBreasts, @Nullable SoundEvent hurtSound) {
			this.name = name;
			this.canHaveBreasts = canHaveBreasts;
			this.hurtSound = hurtSound;
		}

		public Component getDisplayName() {
			return name;
		}

		@Nullable
		public SoundEvent getHurtSound() {
			return hurtSound;
		}

		public boolean canHaveBreasts() {
			return canHaveBreasts;
		}
	}
}
