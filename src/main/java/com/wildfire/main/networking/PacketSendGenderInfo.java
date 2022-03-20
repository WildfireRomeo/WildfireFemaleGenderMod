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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PacketSendGenderInfo extends PacketGenderInfo {

    public PacketSendGenderInfo(GenderPlayer plr) {
        super(plr);
    }

    public PacketSendGenderInfo(PacketByteBuf buffer) {
        super(buffer);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PacketSendGenderInfo packet = new PacketSendGenderInfo(buf);

        if (player == null || !player.getUuid().equals(packet.uuid)) {
            //Validate the uuid matches the player who sent it
            return;
        }
        GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
        packet.updatePlayerFromPacket(plr);
        //System.out.println("Received data from player " + plr.uuid);
        //Sync changes to other online players
        PacketSync.sendToOthers(player, plr);
    }

    // Send Packet

    public static void send(GenderPlayer plr) {
        if(plr == null || !plr.needsSync) return;
        PacketSendGenderInfo packet = new PacketSendGenderInfo(plr);
        PacketByteBuf buffer = PacketByteBufs.create();
        packet.encode(buffer);
        ClientPlayNetworking.send(new Identifier(WildfireGender.MODID, "send_gender_info"), buffer);
        //WildfireGender.NETWORK.sendToServer(new PacketSendGenderInfo(plr));
        plr.needsSync = false;
    }
}
