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

public class WildfireGenderServer implements ModInitializer {

    public static ArrayList<GenderPlayer> CLOTHING_PLAYER = new ArrayList<GenderPlayer>();

    @Override
    public void onInitialize() {

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("wildfire_gender", "send_gender_info"),
        (server, playerEntity, handler, buf, responseSender) -> {

            String uuid = buf.readString(36);
            int gender = buf.readInt();
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

            GenderPlayer plr = getPlayerByName(uuid);
            if(plr == null) {
                plr = new GenderPlayer(uuid);
                CLOTHING_PLAYER.add(plr);
            }
            plr.gender = gender;
            plr.updateBustSize(bust_size);
            plr.hurtSounds = hurtSounds;

            plr.breast_physics = breastPhysics;
            plr.breast_physics_armor = breastPhysicsArmor;
            plr.show_in_armor = showInArmor;

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
                    GenderPlayer aPlr = getPlayerByName(sPlayer.getUuidAsString());
                    if(aPlr == null) return;
                    PacketByteBuf sBuf = PacketByteBufs.create();
                    sBuf.writeString(aPlr.username);
                    sBuf.writeInt(aPlr.gender);
                    sBuf.writeFloat(aPlr.getBustSize());
                    sBuf.writeBoolean(aPlr.hurtSounds);

                    sBuf.writeBoolean(aPlr.breast_physics);
                    sBuf.writeBoolean(aPlr.breast_physics_armor);
                    sBuf.writeBoolean(aPlr.show_in_armor);
                    sBuf.writeFloat(aPlr.getBreasts().xOffset);
                    sBuf.writeFloat(aPlr.getBreasts().yOffset);
                    sBuf.writeFloat(aPlr.getBreasts().zOffset);
                    sBuf.writeFloat(aPlr.getBreasts().cleavage);

                    sBuf.writeBoolean(aPlr.getBreasts().isUniboob);

                    sBuf.writeFloat(aPlr.bounceMultiplier);
                    sBuf.writeFloat(aPlr.floppyMultiplier);
                    if (!player.getUuidAsString().equals(aPlr.username)) {
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

    public static GenderPlayer getPlayerByName(String username) {
        for (int i = 0; i < CLOTHING_PLAYER.size(); i++) {
            try {
                if (username.toLowerCase().equals(((GenderPlayer)CLOTHING_PLAYER.get(i)).username.toLowerCase())) {
                    return (GenderPlayer)CLOTHING_PLAYER.get(i);
                }
            } catch (Exception e) {
                GenderPlayer plr = new GenderPlayer(username);
                CLOTHING_PLAYER.add(plr);
                return plr;
            }
        }

        return null;
    }
}
