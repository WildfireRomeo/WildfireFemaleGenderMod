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

package com.wildfire.main.networking;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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
            if (Minecraft.getInstance().player == null || !packet.uuid.equals(Minecraft.getInstance().player.getUUID())) {
                GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
                packet.updatePlayerFromPacket(plr);
                plr.syncStatus = GenderPlayer.SyncStatus.SYNCED;
                //WildfireGender.logger.debug("Received player data {}", plr.uuid);
            }/* else {
                WildfireGender.logger.debug("Ignoring packet, this is yourself.");
            }*/
        });
        context.get().setPacketHandled(true);
    }
}
