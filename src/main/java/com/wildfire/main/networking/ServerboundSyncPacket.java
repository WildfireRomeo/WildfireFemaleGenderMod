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
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.WildfireGender;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ServerboundSyncPacket extends AbstractSyncPacket {

    public static final CustomPacketPayload.Type<ServerboundSyncPacket> TYPE = new CustomPacketPayload.Type<>(WildfireGender.rl("send_gender_info"));
    public static final StreamCodec<ByteBuf, ServerboundSyncPacket> STREAM_CODEC = streamCodec(ServerboundSyncPacket::new);

    public ServerboundSyncPacket(PlayerConfig plr) {
        super(plr);
    }

    private ServerboundSyncPacket(UUID uuid, Gender gender, float bustSize, boolean hurtSounds, BreastPhysics breastPhysics, Breasts breasts) {
        super(uuid, gender, bustSize, hurtSounds, breastPhysics, breasts);
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        //Validate the uuid matches the player who sent it
        if (player.getUUID().equals(uuid)) {
            PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
            updatePlayerFromPacket(plr);
            //WildfireGender.logger.debug("Received data from player {}", plr.uuid);
            //Sync changes to other online players that are tracking us
            for (ServerPlayer tracker : WildfireGender.getTrackers(player)) {
                PacketDistributor.sendToPlayer(tracker, new ClientboundSyncPacket(plr));
            }
        }
    }

    @NotNull
    @Override
    public CustomPacketPayload.Type<ServerboundSyncPacket> type() {
        return TYPE;
    }
}
