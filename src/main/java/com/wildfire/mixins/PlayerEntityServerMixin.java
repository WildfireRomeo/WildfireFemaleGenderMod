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
package com.wildfire.mixins;
import com.mojang.authlib.GameProfile;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class, priority = 900)
public abstract class PlayerEntityServerMixin extends LivingEntity {


    public PlayerEntityServerMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(EntityType.PLAYER, world);
    }

    @Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
    private void onDamagePlayer(DamageSource source, float amount, CallbackInfo ci) {
        if (!this.isInvulnerableTo(source)) {
            PlayerEntity self = (PlayerEntity) (Object) this;

            amount = this.applyArmorToDamage(source, amount);
            amount = this.modifyAppliedDamage(source, amount);
            float f = amount;
            amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);

            if(amount != 0.0f) {
                //send to client hurt sound?
                GenderPlayer plr = WildfireGender.getPlayerById(self.getUuid());
                if (plr != null) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeUuid(plr.uuid);
                    buf.writeEnumConstant(plr.getGender());
                    buf.writeBoolean(plr.hasHurtSounds());
                    for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, world.getPlayerByUuid(plr.uuid).getBlockPos())) {
                        if (ServerPlayNetworking.canSend(player, new Identifier("wildfire_gender", "hurt"))) {
                            ServerPlayNetworking.send(player, new Identifier("wildfire_gender", "hurt"), buf);
                        }
                    }
                }
            }
        }
    }

}