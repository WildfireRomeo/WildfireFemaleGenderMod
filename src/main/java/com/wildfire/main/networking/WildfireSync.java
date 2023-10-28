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
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class WildfireSync {

    private static final String PROTOCOL_VERSION = "2";
    private static final Predicate<String> ACCEPTED_VERSIONS = NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION);
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(WildfireGender.rl("main_channel"))
          .clientAcceptedVersions(ACCEPTED_VERSIONS)
          .serverAcceptedVersions(ACCEPTED_VERSIONS)
          .networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

    public static void setup(FMLCommonSetupEvent event) {
        NETWORK.registerMessage(1, PacketSync.class, PacketSync::encode, PacketSync::new, PacketSync::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        NETWORK.registerMessage(2, PacketSendGenderInfo.class, PacketSendGenderInfo::encode, PacketSendGenderInfo::new, PacketSendGenderInfo::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /**
     * Sync a player's configuration to all nearby connected players
     *
     * @param toSync       The {@link ServerPlayer player} to sync
     * @param genderPlayer The {@link GenderPlayer configuration} for the target player
     */
    public static void sendToOtherClients(ServerPlayer toSync, GenderPlayer genderPlayer) {
        if (genderPlayer != null && toSync.getServer() != null && !(toSync instanceof FakePlayer)) {
            NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> toSync), new PacketSync(genderPlayer));
        }
    }

    /**
     * Sync a player's configuration to another connected player
     *
     * @param sendTo The {@link ServerPlayer player} to send the sync to
     * @param toSync The {@link GenderPlayer configuration} for the player being synced
     */
    public static void sendToClient(ServerPlayer sendTo, GenderPlayer toSync) {
        if (!(sendTo instanceof FakePlayer)) {
            sendPacketToClient(sendTo, new PacketSync(toSync));
        }
    }

    /**
     * Send the client player's configuration to the server for syncing to other players
     *
     * @param plr The {@link GenderPlayer configuration} for the client player
     */
    public static void sendToServer(GenderPlayer plr) {
        if (plr != null && plr.needsSync) {
            NETWORK.sendToServer(new PacketSendGenderInfo(plr));
            plr.needsSync = false;
        }
    }

    private static void sendPacketToClient(ServerPlayer sendTo, PacketSync packet) {
        if (!(sendTo instanceof FakePlayer)) {
            NETWORK.send(PacketDistributor.PLAYER.with(() -> sendTo), packet);
        }
    }
}