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

package com.wildfire.mixins;

import com.wildfire.main.entitydata.EntityConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ArmorStandEntity.class, PlayerEntity.class})
abstract class BreastPhysicsTickMixin {
	@Inject(at = @At("TAIL"), method = "tick")
	public void wildfiregender$tickBreastPhysics(CallbackInfo info) {
		LivingEntity entity = (LivingEntity)(Object)this;
		// Ignore ticks from the singleplayer integrated server
		if(!entity.getWorld().isClient()) return;

		EntityConfig cfg = EntityConfig.getEntity(entity);
		if(cfg == null) return;
		if(entity instanceof ArmorStandEntity) {
			cfg.readFromStack(entity.getEquippedStack(EquipmentSlot.CHEST));
		}
		cfg.tickBreastPhysics(entity);
	}
}
