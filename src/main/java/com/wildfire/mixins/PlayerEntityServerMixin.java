package com.wildfire.mixins;

import com.mojang.authlib.GameProfile;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGenderServer;
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

import java.util.UUID;

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
            amount = this.applyEnchantmentsToDamage(source, amount);
            float f = amount;
            amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);

            if(amount != 0.0f) {
                //send to client hurt sound?
                GenderPlayer plr = WildfireGenderServer.getPlayerByName(self.getUuidAsString());
                if (plr != null) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeString(plr.username);
                    buf.writeInt(plr.gender);
                    buf.writeBoolean(plr.hurtSounds);
                    for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, world.getPlayerByUuid(UUID.fromString(plr.username)).getBlockPos())) {
                        if (ServerPlayNetworking.canSend(player, new Identifier("wildfire_gender", "hurt"))) {
                            ServerPlayNetworking.send(player, new Identifier("wildfire_gender", "hurt"), buf);
                        }
                    }
                }
            }
        }
    }

}
