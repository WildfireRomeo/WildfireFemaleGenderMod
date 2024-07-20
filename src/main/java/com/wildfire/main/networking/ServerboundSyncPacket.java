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

public final class ServerboundSyncPacket extends AbstractSyncPacket implements CustomPayload {

	public static final Id<ServerboundSyncPacket> ID = new CustomPayload.Id<>(Identifier.of(WildfireGender.MODID, "send_gender_info"));
	public static final PacketCodec<ByteBuf, ServerboundSyncPacket> CODEC = codec(ServerboundSyncPacket::new);

	public ServerboundSyncPacket(PlayerConfig plr) {
		super(plr);
	}

	private ServerboundSyncPacket(UUID uuid, Gender gender, float bustSize, boolean hurtSounds, BreastPhysics physics, Breasts breasts) {
		super(uuid, gender, bustSize, hurtSounds, physics, breasts);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static boolean canSend() {
		return ClientPlayNetworking.canSend(ID);
	}

	public void handle(ServerPlayNetworking.Context context) {
		ServerPlayerEntity player = context.player();
		PlayerConfig plr = WildfireGender.getOrAddPlayerById(player.getUuid());
		updatePlayerFromPacket(plr);
		WildfireSync.sendToAllClients(player, plr);
	}
}
