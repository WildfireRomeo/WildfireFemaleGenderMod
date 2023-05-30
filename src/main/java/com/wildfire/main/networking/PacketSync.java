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

package com.wildfire.main.networking;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketSync extends PacketGenderInfo {

    public PacketSync(GenderPlayer plr) {
        super(plr);
    }

    public PacketSync(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public static void handle(final PacketSync packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (!packet.uuid.equals(Minecraft.getInstance().player.getUUID())) {
                GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
                packet.updatePlayerFromPacket(plr);
                plr.syncStatus = GenderPlayer.SyncStatus.SYNCED;
                plr.lockSettings = true;
                //System.out.println("Received player data " + plr.uuid);
            } else {
                //System.out.println("Ignoring packet, this is yourself.");
            }
        });
        context.get().setPacketHandled(true);
    }

    // Send Packet

    public static void sendToOthers(ServerPlayer player, GenderPlayer genderPlayer) {
        MinecraftServer server = player.getServer();
        if (genderPlayer != null && server != null) {
            PacketSync syncPacket = new PacketSync(genderPlayer);
            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                if (!player.getUUID().equals(serverPlayer.getUUID())) {
                    //Only sync if it isn't the client we are syncing (they already know about themselves)
                    WildfireGender.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), syncPacket);
                }
            }
        }
    }

    public static void sendTo(ServerPlayer player) {
        PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
        for (Map.Entry<UUID, GenderPlayer> entry : WildfireGender.CLOTHING_PLAYERS.entrySet()) {
            UUID uuid = entry.getKey();
            if (!player.getUUID().equals(uuid)) {
                WildfireGender.NETWORK.send(target, new PacketSync(entry.getValue()));
            }
        }
    }
}
