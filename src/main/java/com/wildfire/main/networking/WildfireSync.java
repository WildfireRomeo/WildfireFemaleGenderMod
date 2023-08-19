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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class WildfireSync {
	// While these two identifiers could be combined into one `sync` identifier, this is kept as-is for the sake of compatibility
	// with older versions, and servers that may implement syncing on other platforms (such as Spigot or any of its forks).
	public static final Identifier SEND_GENDER_IDENTIFIER = new Identifier(WildfireGender.MODID, "send_gender_info");
	public static final Identifier SYNC_IDENTIFIER = new Identifier(WildfireGender.MODID, "sync");

	@SuppressWarnings("unused")
	@Environment(EnvType.CLIENT)
	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		SyncPacket packet = new SyncPacket(buf);
		if(client.player == null || packet.uuid.equals(client.player.getUuid())) return;

		GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
		packet.updatePlayerFromPacket(plr);
		plr.syncStatus = GenderPlayer.SyncStatus.SYNCED;
	}

	@SuppressWarnings("unused")
	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		SyncPacket packet = new SyncPacket(buf);
		if(player == null || !player.getUuid().equals(packet.uuid)) return;

		GenderPlayer plr = WildfireGender.getOrAddPlayerById(packet.uuid);
		packet.updatePlayerFromPacket(plr);
		sendToAllClients(player, plr);
	}

	/**
	 * Sync a player's configuration to all nearby connected players
	 *
	 * @param toSync       The {@link ServerPlayerEntity player} to sync
	 * @param genderPlayer The {@link GenderPlayer configuration} for the target player
	 */
	public static void sendToAllClients(ServerPlayerEntity toSync, GenderPlayer genderPlayer) {
	    if(genderPlayer == null || toSync.getServer() == null) return;

	    PacketByteBuf packet = new SyncPacket(genderPlayer).getPacket();
	    PlayerLookup.tracking(toSync).forEach((sendTo) -> {
			if(sendTo.getUuid().equals(toSync.getUuid())) return;
		    sendPacketToClient(sendTo, packet);
	    });
	}

	/**
	 * Sync a player's configuration to another connected player
	 *
	 * @param sendTo The {@link ServerPlayerEntity player} to send the sync to
	 * @param toSync The {@link GenderPlayer configuration} for the player being synced
	 */
	public static void sendToClient(ServerPlayerEntity sendTo, GenderPlayer toSync) {
		sendPacketToClient(sendTo, new SyncPacket(toSync).getPacket());
	}

	/**
	 * Send the client player's configuration to the server for syncing to other players
	 *
	 * @param plr The {@link GenderPlayer configuration} for the client player
	 */
	@Environment(EnvType.CLIENT)
	public static void sendToServer(GenderPlayer plr) {
	    if(plr == null || !plr.needsSync) return;
	    PacketByteBuf buffer = new SyncPacket(plr).getPacket();
	    ClientPlayNetworking.send(SEND_GENDER_IDENTIFIER, buffer);
	    plr.needsSync = false;
	}

	private static void sendPacketToClient(ServerPlayerEntity sendTo, PacketByteBuf packet) {
		if(ServerPlayNetworking.canSend(sendTo, SYNC_IDENTIFIER)) {
			ServerPlayNetworking.send(sendTo, SYNC_IDENTIFIER, packet);
		}
	}
}
