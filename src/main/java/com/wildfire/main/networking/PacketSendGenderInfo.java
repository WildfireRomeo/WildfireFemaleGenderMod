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
import com.wildfire.main.WildfireGender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class PacketSendGenderInfo extends PacketGenderInfo {

    public static final ResourceLocation ID = WildfireGender.rl("send_gender_info");

    public PacketSendGenderInfo(PlayerConfig plr) {
        super(plr);
    }

    public PacketSendGenderInfo(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void handle(IPayloadContext context) {
        context.player()
              //Validate the uuid matches the player who sent it
              .filter(player -> player.getUUID().equals(uuid))
              .ifPresent(player -> {
                  PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
                  updatePlayerFromPacket(plr);
                  //WildfireGender.logger.debug("Received data from player {}", plr.uuid);
                  //Sync changes to other online players that are tracking us
                  for (ServerPlayer tracker : WildfireGender.getTrackers(player)) {
                      PacketDistributor.PLAYER.with(tracker).send(new PacketSync(plr));
                  }
              });
    }

    @NotNull
    @Override
    public ResourceLocation id() {
        return ID;
    }
}
