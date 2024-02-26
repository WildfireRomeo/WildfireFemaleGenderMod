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

package com.wildfire.main.entitydata;

import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A version of {@link EntityConfig} backed by a {@link Configuration} for use with players
 */
@SuppressWarnings("UnusedReturnValue")
public class PlayerConfig extends EntityConfig {

	private final ClientConfiguration cfg;
	public SyncStatus syncStatus = SyncStatus.UNKNOWN;
	public boolean needsSync;

	private boolean hurtSounds = ClientConfiguration.HURT_SOUNDS.getDefault();
	protected boolean showBreastsInArmor = ClientConfiguration.SHOW_IN_ARMOR.getDefault();
	private boolean armorPhysOverride = ClientConfiguration.ARMOR_PHYSICS_OVERRIDE.getDefault();

	public PlayerConfig(UUID uuid) {
		this(uuid, ClientConfiguration.GENDER.getDefault());
	}

	public PlayerConfig(UUID uuid, Gender gender) {
		super(uuid);
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

	// this shouldn't ever be called on players, but just to be safe, override with a noop.
	@Override
	public void readFromStack(@NotNull ItemStack chest) {

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

	public boolean updateGender(Gender value) {
		return updateValue(ClientConfiguration.GENDER, value, v -> this.gender = v);
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

	public boolean updateBreastPhysics(boolean value) {
		return updateValue(ClientConfiguration.BREAST_PHYSICS, value, v -> this.breastPhysics = v);
	}

	@Override
	public boolean getArmorPhysicsOverride() {
		return armorPhysOverride;
	}

	@Override
	public boolean canBreathe() {
		return true;
	}

	public boolean updateArmorPhysicsOverride(boolean value) {
		return updateValue(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, value, v -> this.armorPhysOverride = v);
	}

	@Override
	public boolean showBreastsInArmor() {
		return showBreastsInArmor;
	}

	public boolean updateShowBreastsInArmor(boolean value) {
		return updateValue(ClientConfiguration.SHOW_IN_ARMOR, value, v -> this.showBreastsInArmor = v);
	}

	public boolean updateBounceMultiplier(float value) {
		return updateValue(ClientConfiguration.BOUNCE_MULTIPLIER, value, v -> this.bounceMultiplier = v);
	}

	public boolean updateFloppiness(float value) {
		return updateValue(ClientConfiguration.FLOPPY_MULTIPLIER, value, v -> this.floppyMultiplier = v);
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	public static PlayerConfig loadCachedPlayer(UUID uuid, boolean markForSync) {
		PlayerConfig plr = WildfireGender.getPlayerById(uuid);
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
	
	public static void saveGenderInfo(PlayerConfig plr) {
		ClientConfiguration config = plr.getConfig();
		config.set(ClientConfiguration.USERNAME, plr.uuid);
		config.set(ClientConfiguration.GENDER, plr.getGender());
		config.set(ClientConfiguration.BUST_SIZE, plr.getBustSize());
		config.set(ClientConfiguration.HURT_SOUNDS, plr.hasHurtSounds());

		//physics
		config.set(ClientConfiguration.BREAST_PHYSICS, plr.hasBreastPhysics());
		config.set(ClientConfiguration.SHOW_IN_ARMOR, plr.showBreastsInArmor());
		config.set(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, plr.getArmorPhysicsOverride());
		config.set(ClientConfiguration.BOUNCE_MULTIPLIER, plr.getBounceMultiplier());
		config.set(ClientConfiguration.FLOPPY_MULTIPLIER, plr.getFloppiness());

		config.set(ClientConfiguration.BREASTS_OFFSET_X, plr.getBreasts().getXOffset());
		config.set(ClientConfiguration.BREASTS_OFFSET_Y, plr.getBreasts().getYOffset());
		config.set(ClientConfiguration.BREASTS_OFFSET_Z, plr.getBreasts().getZOffset());
		config.set(ClientConfiguration.BREASTS_UNIBOOB, plr.getBreasts().isUniboob());
		config.set(ClientConfiguration.BREASTS_CLEAVAGE, plr.getBreasts().getCleavage());

		config.save();
		plr.needsSync = true;
	}

	@Override
	public boolean hasJacketLayer() {
		throw new UnsupportedOperationException("PlayerConfig does not support #hasJacketLayer(); use Player#isModelPartShown instead");
	}

	public enum SyncStatus {
		CACHED, SYNCED, UNKNOWN
	}
}
