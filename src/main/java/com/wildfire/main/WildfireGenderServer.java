package com.wildfire.main;
/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WildfireGenderServer implements ModInitializer {

    public static ArrayList<GenderPlayer> CLOTHING_PLAYER = new ArrayList<GenderPlayer>();

    @Override
    public void onInitialize() {

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("wildfire_gender", "send_gender_info"),
        (server, playerEntity, handler, buf, responseSender) -> {

            UUID uuid = playerEntity.getUuid();
            GenderPlayer.Gender gender = buf.readEnumConstant(GenderPlayer.Gender.class);
            float bust_size = buf.readFloat();
            boolean hurtSounds = buf.readBoolean();
            boolean breastPhysics = buf.readBoolean();
            boolean breastPhysicsArmor = buf.readBoolean();
            boolean showInArmor = buf.readBoolean();

            float breastOffsetX = buf.readFloat();
            float breastOffsetY = buf.readFloat();
            float breastOffsetZ = buf.readFloat();
            float breastCleavage = buf.readFloat();

            boolean breastDualPhysics = buf.readBoolean();

            float bounceMultiplier = buf.readFloat();
            float floppyMultiplier = buf.readFloat();

            ServerPlayerEntity player = playerEntity;
            if(!player.getUuidAsString().equals(uuid)) return;

            GenderPlayer plr = WildfireGender.getPlayerById(uuid);
            if(plr == null) {
                plr = new GenderPlayer(uuid);
                CLOTHING_PLAYER.add(plr);
            }
            plr.gender = gender;
            plr.updateBustSize(bust_size);
            plr.hurtSounds = hurtSounds;

            plr.hasBreastPhysics = breastPhysics;
            plr.hasArmorBreastPhysics = breastPhysicsArmor;
            plr.showBreastsInArmor = showInArmor;

            plr.getBreasts().xOffset = breastOffsetX;
            plr.getBreasts().yOffset = breastOffsetY;
            plr.getBreasts().zOffset = breastOffsetZ;
            plr.getBreasts().cleavage = breastCleavage;
            plr.getBreasts().isUniboob = breastDualPhysics;

            plr.bounceMultiplier = bounceMultiplier;
            plr.floppyMultiplier = floppyMultiplier;

            try {
                List<ServerPlayerEntity> PLAYERS = player.getServer().getPlayerManager().getPlayerList();
                for(ServerPlayerEntity sPlayer : PLAYERS) {
                    GenderPlayer aPlr = getPlayerById(sPlayer.getUuid());
                    if(aPlr == null) return;
                    PacketByteBuf sBuf = PacketByteBufs.create();
                    sBuf.writeEnumConstant(aPlr.gender);
                    sBuf.writeFloat(aPlr.getBustSize());
                    sBuf.writeBoolean(aPlr.hurtSounds);

                    sBuf.writeBoolean(aPlr.hasBreastPhysics);
                    sBuf.writeBoolean(aPlr.hasArmorBreastPhysics);
                    sBuf.writeBoolean(aPlr.showBreastsInArmor);
                    sBuf.writeFloat(aPlr.getBreasts().xOffset);
                    sBuf.writeFloat(aPlr.getBreasts().yOffset);
                    sBuf.writeFloat(aPlr.getBreasts().zOffset);
                    sBuf.writeFloat(aPlr.getBreasts().cleavage);

                    sBuf.writeBoolean(aPlr.getBreasts().isUniboob);

                    sBuf.writeFloat(aPlr.bounceMultiplier);
                    sBuf.writeFloat(aPlr.floppyMultiplier);
                    if (!player.getUuid().equals(aPlr.uuid)) {
                        if (aPlr == null) return;
                        if (ServerPlayNetworking.canSend(player, new Identifier("wildfire_gender", "sync"))) {
                            ServerPlayNetworking.send(player, new Identifier("wildfire_gender", "sync"), sBuf);
                        }
                    }
                    //send(player, WildfireGender.getPlayerByName(sPlayer.getUuidAsString());
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static GenderPlayer getPlayerById(UUID uuid) {
        for (int i = 0; i < CLOTHING_PLAYER.size(); i++) {
            try {
                if (uuid.equals(CLOTHING_PLAYER.get(i).uuid)) {
                    return CLOTHING_PLAYER.get(i);
                }
            } catch (Exception e) {
                GenderPlayer plr = new GenderPlayer(uuid);
                CLOTHING_PLAYER.add(plr);
                return plr;
            }
        }

        return null;
    }
}
