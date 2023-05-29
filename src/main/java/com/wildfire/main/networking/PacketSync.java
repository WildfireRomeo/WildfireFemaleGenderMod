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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PacketSync extends PacketGenderInfo {
    public PacketSync(GenderPlayer plr) {
        super(plr);
    }

    public PacketSync(PacketByteBuf buffer) {
        super(buffer);
    }

    /**
     * Method called when the client receives a sync packet for another player from the server
     */
    @SuppressWarnings("unused")
    @Environment(EnvType.CLIENT)
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(client.player == null) {
            WildfireGender.LOGGER.warn("Received a sync packet from the server while the client player was unset; discarding");
            return;
        }
        PacketSync packet = new PacketSync(buf);
        if(packet.uuid.equals(client.player.getUuid())) return;

        GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
        packet.updatePlayerFromPacket(plr);
        plr.syncStatus = GenderPlayer.SyncStatus.SYNCED;
        plr.lockSettings = true;
    }

    /**
     * <p>Sync a given player's settings to all other players</p>
     *
     * <p><b>Note:</b> This will only sync to other players currently within render distance of the given player</p>
     *
     * @param    player  The {@link ServerPlayerEntity} to sync
     * @param    genderPlayer  The {@link GenderPlayer} class for the given {@code player}
     */
    public static void syncToOthers(ServerPlayerEntity player, GenderPlayer genderPlayer) {
        if(genderPlayer == null) return;

        PacketSync packet = new PacketSync(genderPlayer);
        PacketByteBuf buffer = PacketByteBufs.create();
        packet.encode(buffer);

        PlayerLookup.tracking(player).forEach((sendTo) -> {
            if(sendTo.getUuid().equals(player.getUuid())) return;
            syncTo(buffer, sendTo);
        });
    }

    /**
     * Sync a player's gender settings to a single player
     *
     * @param    toSync  The {@link GenderPlayer} to sync
     * @param    syncTo  The {@link ServerPlayerEntity} to send the given player's settings to
     */
    public static void syncTo(GenderPlayer toSync, ServerPlayerEntity syncTo) {
        if(toSync.uuid == syncTo.getUuid()) return;
        PacketSync packet = new PacketSync(toSync);
        PacketByteBuf buffer = PacketByteBufs.create();
        packet.encode(buffer);
        syncTo(buffer, syncTo);
    }

    private static void syncTo(PacketByteBuf packet, ServerPlayerEntity sendTo) {
        if(!ServerPlayNetworking.canSend(sendTo, WildfireGender.id("sync"))) return;
        ServerPlayNetworking.send(sendTo, WildfireGender.id("sync"), packet);
    }
}
