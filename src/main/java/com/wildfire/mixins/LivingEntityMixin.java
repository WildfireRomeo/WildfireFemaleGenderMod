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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * A note on why this implementation in particular was chosen:
 *
 * While this could be reduced down to one mixin (the `#onDamaged(DamageSource)` one in particular), and forego any sort
 * of server-side handling, this is being done as (at least as of when this is being written,) the mod fails to ever
 * perform an initial sync upon a player joining a dedicated server; as such, we're using client- *and* server-side mixins
 * to provide some level of consistency, even if syncing isn't consistent.
 *
 * We're additionally playing *alongside* the vanilla hurt sound, largely for the sake of accessibility, as the vanilla
 * hurt sound may provide important context as for why a player is taking damage, which could prove especially helpful
 * for players with poor/no eyesight.
 *
 * Additionally, completely replacing the hurt sound server-side would essentially require the mod client-side as well
 * to hear *any* hurt sounds from players with this setting enabled, which rules out mixins to methods such as
 * `PlayerEntity#getHurtSound(DamageSource)`.
 */
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
	public void clientGenderHurtSound(DamageSource damageSource, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player == null || client.world == null) return;

		if((LivingEntity)(Object)this instanceof PlayerEntity player) {
			if(player.getWorld().isClient() && player.getUuid().equals(client.player.getUuid())) {
				this.playGenderHurtSound(player);
			}
		}
	}

	@Inject(
		method = "damage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;playHurtSound(Lnet/minecraft/entity/damage/DamageSource;)V"
		)
	)
	public void serverGenderHurtSound(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if((LivingEntity)(Object)this instanceof PlayerEntity player) {
			if(!player.getWorld().isClient()) this.playGenderHurtSound(player);
		}
	}

	@Unique
	private void playGenderHurtSound(PlayerEntity player) {
		GenderPlayer genderPlayer = WildfireGender.getPlayerById(player.getUuid());
		if(genderPlayer == null || !genderPlayer.hasHurtSounds()) return;

		SoundEvent hurtSound = genderPlayer.getGender().getHurtSound();
		if(hurtSound != null) {
			float pitch = (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F;
			player.playSound(hurtSound, 1f, pitch);
		}
	}
}
