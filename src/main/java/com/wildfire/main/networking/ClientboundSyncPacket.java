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
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ClientboundSyncPacket extends AbstractSyncPacket {

    public static final CustomPacketPayload.Type<ClientboundSyncPacket> TYPE = new CustomPacketPayload.Type<>(WildfireGender.rl("sync"));
    public static final StreamCodec<ByteBuf, ClientboundSyncPacket> STREAM_CODEC = streamCodec(ClientboundSyncPacket::new);

    public ClientboundSyncPacket(PlayerConfig plr) {
        super(plr);
    }

    private ClientboundSyncPacket(UUID uuid, Gender gender, float bustSize, boolean hurtSounds, BreastPhysics breastPhysics, Breasts breasts) {
        super(uuid, gender, bustSize, hurtSounds, breastPhysics, breasts);
    }

    @Override
    public void handle(IPayloadContext context) {
        //Validate it is a different player
        if (!context.player().getUUID().equals(uuid)) {
            PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
            updatePlayerFromPacket(plr);
            plr.syncStatus = PlayerConfig.SyncStatus.SYNCED;
            //WildfireGender.logger.debug("Received player data {}", plr.uuid);
        }
    }

    @NotNull
    @Override
    public CustomPacketPayload.Type<ClientboundSyncPacket> type() {
        return TYPE;
    }
}
