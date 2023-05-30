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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendGenderInfo extends PacketGenderInfo {

    public PacketSendGenderInfo(GenderPlayer plr) {
        super(plr);
    }

    public PacketSendGenderInfo(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public static void handle(final PacketSendGenderInfo packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || !player.getUUID().equals(packet.uuid)) {
                //Validate the uuid matches the player who sent it
                return;
            }
            GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
            packet.updatePlayerFromPacket(plr);
            //System.out.println("Received data from player " + plr.uuid);
            //Sync changes to other online players
            PacketSync.sendToOthers(player, plr);
        });

        context.get().setPacketHandled(true);
    }

    // Send Packet

    public static void send(GenderPlayer plr) {
        if(plr == null || !plr.needsSync) return;
        WildfireGender.NETWORK.sendToServer(new PacketSendGenderInfo(plr));
        plr.needsSync = false;
    }
}
