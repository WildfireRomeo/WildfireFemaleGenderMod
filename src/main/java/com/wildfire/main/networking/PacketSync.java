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

package com.wildfire.main.networking;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketSync extends PacketGenderInfo {

    public PacketSync(GenderPlayer plr) {
        super(plr);
    }

    public PacketSync(PacketByteBuf buffer) {
        super(buffer);
    }

    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PacketSync packet = new PacketSync(buf);

        if (!packet.uuid.equals(MinecraftClient.getInstance().player.getUuid())) {
            GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
            packet.updatePlayerFromPacket(plr);
            plr.syncStatus = GenderPlayer.SyncStatus.SYNCED;
            plr.lockSettings = true;
            //System.out.println("Received player data " + plr.uuid);
        } else {
            //System.out.println("Ignoring packet, this is yourself.");
        }
    }

    // Send Packet

    public static void sendToOthers(ServerPlayerEntity player, GenderPlayer genderPlayer) {
        if (genderPlayer != null && player.getServer() != null) {
            PacketSync packet = new PacketSync(genderPlayer);
            PacketByteBuf buffer = PacketByteBufs.create();
            packet.encode(buffer);

            for (ServerPlayerEntity serverPlayer : PlayerLookup.all(player.getServer())) {
                if (!player.getUuid().equals(serverPlayer.getUuid())) {
                    ServerPlayNetworking.send(serverPlayer, new Identifier(WildfireGender.MODID, "sync"), buffer);
                }
            }
        }
    }

    public static void sendTo(ServerPlayerEntity player) {
        for (Map.Entry<UUID, GenderPlayer> entry : WildfireGender.CLOTHING_PLAYERS.entrySet()) {
            UUID uuid = entry.getKey();
            if (!player.getUuid().equals(uuid)) {
                PacketSync packet = new PacketSync(entry.getValue());
                PacketByteBuf buffer = PacketByteBufs.create();
                packet.encode(buffer);
                ServerPlayNetworking.send(player, new Identifier(WildfireGender.MODID, "sync"), buffer);
            }
        }
    }
}
