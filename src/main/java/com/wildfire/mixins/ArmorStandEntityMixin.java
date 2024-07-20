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

import com.llamalad7.mixinextras.sugar.Local;
import com.wildfire.api.IGenderArmor;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.BreastDataComponent;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.WildfireHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin extends LivingEntity {
	protected ArmorStandEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyArg(
		method = "equip",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"
		),
		index = 1
	)
	public ItemStack wildfiregender$attachBreastData(ItemStack stack, @Local(argsOnly = true) EquipmentSlot slot,
	                                                 @Local(argsOnly = true) PlayerEntity player) {
		if(player == null || player.getWorld().isClient() || slot != EquipmentSlot.CHEST) {
			return stack;
		}

		PlayerConfig playerConfig = WildfireGender.getPlayerById(player.getUuid());
		if(playerConfig == null) {
			// while we shouldn't have our tag on the stack still, we're still checking to catch any armor
			// that may still have the tag from older versions, or from potential cross-mod interactions
			// which allow for removing items from armor stands without calling the vanilla
			// #equip and/or #onBreak methods
			BreastDataComponent.removeFromStack(stack);
			return stack;
		}

		IGenderArmor armorConfig = WildfireHelper.getArmorConfig(stack);
		if(armorConfig.armorStandsCopySettings()) {
			BreastDataComponent component = BreastDataComponent.fromPlayer(player, playerConfig);
			if(component != null) {
				component.write(player.getWorld().getRegistryManager(), stack);
			}
		}

		return stack;
	}

	@ModifyArg(
		method = "equip",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"
		),
		index = 1
	)
	public ItemStack wildfiregender$removeBreastDataOnReplace(ItemStack stack, @Local(argsOnly = true) PlayerEntity player) {
		if(!player.getWorld().isClient()) {
			BreastDataComponent.removeFromStack(stack);
		}
		return stack;
	}

	@ModifyArg(
		method = "onBreak",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"
		),
		index = 2
	)
	public ItemStack wildfiregender$removeBreastDataOnBreak(ItemStack stack) {
		if(!getWorld().isClient()) {
			BreastDataComponent.removeFromStack(stack);
		}
		return stack;
	}
}
