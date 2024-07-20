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

import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public final class ClientboundSyncPacket extends AbstractSyncPacket implements CustomPayload {

	public static final Id<ClientboundSyncPacket> ID = new CustomPayload.Id<>(Identifier.of(WildfireGender.MODID, "sync"));
	public static final PacketCodec<ByteBuf, ClientboundSyncPacket> CODEC = codec(ClientboundSyncPacket::new);

	public ClientboundSyncPacket(PlayerConfig plr) {
		super(plr);
	}

	private ClientboundSyncPacket(UUID uuid, Gender gender, float bustSize, boolean hurtSounds, BreastPhysics physics, Breasts breasts) {
		super(uuid, gender, bustSize, hurtSounds, physics, breasts);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static boolean canSend(ServerPlayerEntity player) {
		return ServerPlayNetworking.canSend(player, ID);
	}

	@Environment(EnvType.CLIENT)
	public void handle(ClientPlayNetworking.Context context) {
		if(context.player().getUuid().equals(uuid)) {
			WildfireGender.LOGGER.warn("Ignoring sync packet referring to the client player");
			return;
		}

		PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
		updatePlayerFromPacket(plr);
		plr.syncStatus = PlayerConfig.SyncStatus.SYNCED;
	}
}
