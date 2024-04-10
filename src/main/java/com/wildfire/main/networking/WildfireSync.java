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

import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

public class WildfireSync {
	@ApiStatus.Internal
	public static void register() {
		// note that each packet has to be registered on both sides for receiving and sending, regardless
		// of if the current side is actually supposed to be doing either action for a given packet.
		PayloadTypeRegistry.playC2S().register(ClientboundSyncPacket.ID, ClientboundSyncPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundSyncPacket.ID, ClientboundSyncPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(ServerboundSyncPacket.ID, ServerboundSyncPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ServerboundSyncPacket.ID, ServerboundSyncPacket.CODEC);

		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			ServerPlayNetworking.registerReceiver(handler, ServerboundSyncPacket.ID, ServerboundSyncPacket::handle);
		});
	}

	@ApiStatus.Internal
	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ClientboundSyncPacket.ID, ClientboundSyncPacket::handle);
		});
	}

	/**
	 * Sync a player's configuration to all nearby connected players
	 *
	 * @param toSync       The {@link ServerPlayerEntity player} to sync
	 * @param playerConfig The {@link PlayerConfig configuration} for the target player
	 */
	public static void sendToAllClients(ServerPlayerEntity toSync, PlayerConfig playerConfig) {
		if(playerConfig == null || toSync.getServer() == null) return;

		PlayerLookup.tracking(toSync).stream()
				.filter(player -> !player.equals(toSync))
				.filter(ClientboundSyncPacket::canSend)
				.forEach(player -> ServerPlayNetworking.send(player, new ClientboundSyncPacket(playerConfig)));
	}

	/**
	 * Sync a player's configuration to another connected player
	 *
	 * @param sendTo The {@link ServerPlayerEntity player} to send the sync to
	 * @param toSync The {@link PlayerConfig configuration} for the player being synced
	 */
	public static void sendToClient(ServerPlayerEntity sendTo, PlayerConfig toSync) {
		if(ClientboundSyncPacket.canSend(sendTo)) {
			ServerPlayNetworking.send(sendTo, new ClientboundSyncPacket(toSync));
		}
	}

	/**
	 * Send the client player's configuration to the server for syncing to other players
	 *
	 * @param plr The {@link PlayerConfig configuration} for the client player
	 */
	@Environment(EnvType.CLIENT)
	public static void sendToServer(PlayerConfig plr) {
		if(plr == null || !plr.needsSync || !ServerboundSyncPacket.canSend()) {
			return;
		}

		ClientPlayNetworking.send(new ServerboundSyncPacket(plr));
		plr.needsSync = false;
	}
}
