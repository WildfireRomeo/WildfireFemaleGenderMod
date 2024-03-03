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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class WildfireSync {
	/**
	 * Sync a player's configuration to all nearby connected players
	 *
	 * @param toSync       The {@link ServerPlayerEntity player} to sync
	 * @param playerConfig The {@link PlayerConfig configuration} for the target player
	 */
	public static void sendToAllClients(ServerPlayerEntity toSync, PlayerConfig playerConfig) {
		if(playerConfig == null || toSync.getServer() == null) return;

		SyncToClientPacket syncPacket = new SyncToClientPacket(playerConfig);
		PlayerLookup.tracking(toSync).forEach((sendTo) -> {
            if (!sendTo.getUuid().equals(toSync.getUuid()) && ServerPlayNetworking.canSend(sendTo, syncPacket.getType())) {
                ServerPlayNetworking.send(sendTo, syncPacket);
            }
        });
	}

	/**
	 * Sync a player's configuration to another connected player
	 *
	 * @param sendTo The {@link ServerPlayerEntity player} to send the sync to
	 * @param toSync The {@link PlayerConfig configuration} for the player being synced
	 */
	public static void sendToClient(ServerPlayerEntity sendTo, PlayerConfig toSync) {
		if(ServerPlayNetworking.canSend(sendTo, SyncToClientPacket.PACKET_TYPE)) {
			ServerPlayNetworking.send(sendTo, new SyncToClientPacket(toSync));
		}
	}

	/**
	 * Send the client player's configuration to the server for syncing to other players
	 *
	 * @param plr The {@link PlayerConfig configuration} for the client player
	 */
	@Environment(EnvType.CLIENT)
	public static void sendToServer(PlayerConfig plr) {
	    if(plr == null || !plr.needsSync) return;
	    ClientPlayNetworking.send(new SyncToServerPacket(plr));
	    plr.needsSync = false;
	}
}
