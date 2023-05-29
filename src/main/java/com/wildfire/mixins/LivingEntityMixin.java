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

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Environment(EnvType.CLIENT)
	@Inject(
		method = "onDamaged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"
		)
	)
	public void wildfiregender$clientGenderHurtSound(DamageSource damageSource, CallbackInfo ci) {
		if((LivingEntity)(Object)this instanceof PlayerEntity player) {
			// Only act on the client player; the server will send the hurt sound separately for other players
			// in the below mixin
			MinecraftClient client = MinecraftClient.getInstance();
			if(!player.world.isClient() || client.player == null || !client.player.getUuid().equals(player.getUuid())) {
				return;
			}
			wildfiregender$playHurtSound(player);
		}
	}

	@Inject(
		method = "damage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;playHurtSound(Lnet/minecraft/entity/damage/DamageSource;)V"
		)
	)
	public void wildfiregender$serverGenderHurtSound(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if((LivingEntity)(Object)this instanceof PlayerEntity player) {
			// While this is never called client-side for the client player due to ClientPlayerEntity overriding this
			// method, it is called client-side for *other* players; as such, we need to check this to ensure we don't
			// play double hurt sounds when the mod is installed on the server as well.
			if(player.world.isClient()) return;
			wildfiregender$playHurtSound(player);
		}
	}

	private void wildfiregender$playHurtSound(PlayerEntity player) {
		GenderPlayer genderPlayer = WildfireGender.getPlayerById(player.getUuid());
		if(genderPlayer == null || !genderPlayer.hasHurtSounds()) return;

		SoundEvent hurtSound = genderPlayer.getGender().getHurtSound();
		if(hurtSound != null) {
			float pitch = (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F;
			player.playSound(hurtSound, 1f, pitch);
		}
	}
}
