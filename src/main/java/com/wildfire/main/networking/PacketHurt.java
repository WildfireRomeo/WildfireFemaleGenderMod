package com.wildfire.main.networking;
/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

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
import com.mojang.math.Vector3d;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketHurt {
    private final String uuid;
    private boolean hurtSounds;
    private final int gender;
    private Vec3 pos;

    public PacketHurt(Vec3 pos, GenderPlayer plr) {
        this.uuid = plr.username;
        this.gender = plr.gender;
        this.hurtSounds = plr.hurtSounds;
        this.pos = pos;
    }

    public PacketHurt(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUtf(36);
        this.gender = buffer.readInt();
        this.pos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.hurtSounds = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.uuid);
        buffer.writeInt(this.gender);
        buffer.writeDouble(this.pos.x);
        buffer.writeDouble(this.pos.y);
        buffer.writeDouble(this.pos.z);
        buffer.writeBoolean(this.hurtSounds);
    }

    public static void handle(final PacketHurt packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            GenderPlayer plr = WildfireGender.getPlayerByName(packet.uuid);
            if(plr == null) return;
            if(context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                //play the sound

                SoundEvent hurtSound = null;
                if(packet.gender == 0) {
                    hurtSound = Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2;
                }
                if(hurtSound == null) return;
                if(packet.hurtSounds) {
                    Player ent = Minecraft.getInstance().level.getPlayerByUUID(UUID.fromString(packet.uuid));
                    if (ent != null) {
                        WildfireGender.PROXY.playSound(hurtSound, SoundSource.PLAYERS, 1f, 1f, ent);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    // Send Packet

    public static void send(boolean toServer, Vec3 pos, GenderPlayer plr) {
        if(plr == null) return;
        PacketHurt syncPacket = new PacketHurt(pos, plr);
        if(toServer) {
            WildfireGender.NETWORK.sendToServer(syncPacket);
        } else {
            WildfireGender.NETWORK.send(PacketDistributor.ALL.noArg(), syncPacket);
        }
    }
}
