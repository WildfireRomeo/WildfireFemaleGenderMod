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

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>A stripped down version of a {@link PlayerConfig player's config}, intended for use with non-player entities.</p>
 *
 * <p>Unlike players, this has very minimal configuration support.</p>
 *
 * <p>Currently only used for {@link ArmorStand armor stands}, and as a superclass for {@link PlayerConfig player configs}.</p>
 */
public class EntityConfig {

	public static final Map<UUID, EntityConfig> ENTITY_CACHE = new HashMap<>();

	public final UUID uuid;
	protected Gender gender = ClientConfiguration.GENDER.getDefault();
	protected float pBustSize = ClientConfiguration.BUST_SIZE.getDefault();
	protected boolean breastPhysics = ClientConfiguration.BREAST_PHYSICS.getDefault();
	protected float bounceMultiplier = ClientConfiguration.BOUNCE_MULTIPLIER.getDefault();
	protected float floppyMultiplier = ClientConfiguration.FLOPPY_MULTIPLIER.getDefault();
	// note: hurt sounds and armor physics override are not defined here, as they have no relevance
	// to entities, and are instead entirely in PlayerConfig
	protected final BreastPhysics lBreastPhysics, rBreastPhysics;
	protected final Breasts breasts;
	protected boolean jacketLayer = true;

	EntityConfig(UUID uuid) {
		this.uuid = uuid;
		this.breasts = new Breasts();
		lBreastPhysics = new BreastPhysics(this);
		rBreastPhysics = new BreastPhysics(this);
	}

	/**
	 * Copy gender settings included in the given {@link ItemStack item NBT} to the current entity
	 *
	 * @see WildfireHelper#writeToNbt
	 */
	public void readFromStack(@NotNull ItemStack chestplate) {
		CompoundTag nbt = !chestplate.isEmpty() ? chestplate.getTagElement("WildfireGender") : null;
		if(nbt == null) {
			this.gender = Gender.MALE;
			return;
		}
		this.pBustSize = nbt.contains("BreastSize") ? nbt.getFloat("BreastSize") : 0f;
		this.gender = this.pBustSize > 0.02f ? Gender.FEMALE : Gender.MALE;
		if(nbt.contains("Cleavage")) breasts.updateCleavage(nbt.getFloat("Cleavage"));
		if(nbt.contains("Uniboob")) breasts.updateUniboob(nbt.getBoolean("Uniboob"));
		if(nbt.contains("XOffset")) breasts.updateXOffset(nbt.getFloat("XOffset"));
		if(nbt.contains("YOffset")) breasts.updateYOffset(nbt.getFloat("YOffset"));
		if(nbt.contains("ZOffset")) breasts.updateZOffset(nbt.getFloat("ZOffset"));
		if(nbt.contains("Jacket")) jacketLayer = nbt.getBoolean("Jacket");
	}

	/**
	 * Get the configuration for a given entity
	 *
	 * @return {@link EntityConfig}, {@link PlayerConfig} if given a {@link Player player},
	 *         or {@code null} if given a baby entity
	 */
	public static @Nullable EntityConfig getEntity(@NotNull LivingEntity entity) {
		if (entity instanceof Player) {
			return WildfireGender.getPlayerById(entity.getUUID());
		}
		if (entity.isBaby()) {
			// rendering breaks quite spectacularly on baby mobs, so just immediately give up
			return null;
		}
		return ENTITY_CACHE.computeIfAbsent(entity.getUUID(), EntityConfig::new);
	}

	public @NotNull Gender getGender() {
		return gender;
	}

	public @NotNull Breasts getBreasts() {
		return breasts;
	}

	public float getBustSize() {
		return pBustSize;
	}

	public boolean hasBreastPhysics() {
		return breastPhysics;
	}

	public boolean getArmorPhysicsOverride() {
		return false;
	}

	public boolean canBreathe() {
		return false;
	}

	public boolean showBreastsInArmor() {
		return true;
	}

	public float getBounceMultiplier() {
		return bounceMultiplier;
	}

	public float getFloppiness() {
		return this.floppyMultiplier;
	}

	public @NotNull BreastPhysics getLeftBreastPhysics() {
		return lBreastPhysics;
	}
	public @NotNull BreastPhysics getRightBreastPhysics() {
		return rBreastPhysics;
	}

	/**
	 * Only used in the case of {@link ArmorStand armor stands}; returns {@code true} if the player who equipped
	 * the armor stand's chestplate has their jacket layer visible.
	 */
	public boolean hasJacketLayer() {
		return jacketLayer;
	}

	public void tickBreastPhysics(@NotNull LivingEntity entity) {
		IGenderArmor armor = WildfireHelper.getArmorConfig(entity.getItemBySlot(EquipmentSlot.CHEST));

		getLeftBreastPhysics().update(entity, armor);
		getRightBreastPhysics().update(entity, armor);
	}

	@Override
	public String toString() {
		return "%s(uuid=%s, gender=%s)".formatted(getClass().getCanonicalName(), uuid, gender);
	}
}
