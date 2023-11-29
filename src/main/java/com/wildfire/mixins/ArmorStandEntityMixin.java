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

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.WildfireHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {
	@Inject(
		method = "equip",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V",
			shift = At.Shift.BEFORE
		)
	)
	public void wildfiregender$equipArmorStandChestplate(PlayerEntity player, EquipmentSlot slot, ItemStack stack, Hand hand, CallbackInfoReturnable<Boolean> cir) {
		if(player == null || player.getWorld().isClient()) return;

		Item item = stack.getItem();
		// Only apply to chestplates
		if(!(item instanceof ArmorItem armorItem) || armorItem.getSlotType() != EquipmentSlot.CHEST) return;

		PlayerConfig playerConfig = WildfireGender.getPlayerById(player.getUuid());
		if(playerConfig == null) {
			if(stack.getSubNbt("WildfireGender") != null) {
				stack.removeSubNbt("WildfireGender");
			}
			return;
		}

		IGenderArmor armorConfig = WildfireHelper.getArmorConfig(stack);
		if(armorConfig.armorStandsCopySettings()) {
			WildfireHelper.writeToNbt(player, playerConfig, stack);
		}
	}
}
