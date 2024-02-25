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

package com.wildfire.main;

import com.mojang.logging.LogUtils;
import com.wildfire.main.config.GeneralClientConfig;
import com.wildfire.main.networking.PacketSync;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

@Mod(WildfireGender.MODID)
public class WildfireGender {

	public static final String VERSION = "3.0.1";
  	public static final String MODID = "wildfire_gender";
	public static final Logger logger = LogUtils.getLogger();

	public static Map<UUID, GenderPlayer> CLOTHING_PLAYERS = new HashMap<>();
	private static WildfireGender instance;

	//Tracked player to the set of tracking players
	private final Map<UUID, Set<ServerPlayer>> trackedPlayers = new HashMap<>();

  	public WildfireGender(ModContainer modContainer, IEventBus modEventBus) {
		instance = this;

		modEventBus.addListener(WildfireHelper::registerCapabilities);
		modEventBus.addListener(WildfireHelper::registerPackets);
		NeoForge.EVENT_BUS.addListener(this::onStartTracking);
		NeoForge.EVENT_BUS.addListener(this::onStopTracking);

		if (FMLEnvironment.dist.isClient()) {
			GeneralClientConfig.register(modContainer);
		}
	}

	public static ResourceLocation rl(String path) {
		  return new ResourceLocation(MODID, path);
	}

	public static Set<ServerPlayer> getTrackers(Player target) {
		  return instance.trackedPlayers.getOrDefault(target.getUUID(), Set.of());
	}

	@Nullable
	public static GenderPlayer getPlayerById(UUID id) {
		  return CLOTHING_PLAYERS.get(id);
	}

	public static GenderPlayer getOrAddPlayerById(UUID id) {
		return CLOTHING_PLAYERS.computeIfAbsent(id, GenderPlayer::new);
	}

	private void onStartTracking(PlayerEvent.StartTracking evt) {
		if (evt.getTarget() instanceof Player toSync && evt.getEntity() instanceof ServerPlayer sendTo && sendTo.connection.isConnected(PacketSync.ID)) {
			trackedPlayers.computeIfAbsent(toSync.getUUID(), uuid -> new HashSet<>()).add(sendTo);

			GenderPlayer genderToSync = WildfireGender.getPlayerById(toSync.getUUID());
			if(genderToSync == null) return;
			// Note that we intentionally don't check if we've previously synced a player with this code path;
			// because we use entity tracking to sync, it's entirely possible that one player would leave the
			// tracking distance of another, change their settings, and then re-enter their tracking distance;
			// we wouldn't sync while they're out of tracking distance, and as such, their settings would be out
			// of sync until they relog.
			PacketDistributor.PLAYER.with(sendTo).send(new PacketSync(genderToSync));
		}
	}

	private void onStopTracking(PlayerEvent.StopTracking evt) {
		if (evt.getTarget() instanceof Player toSync && evt.getEntity() instanceof ServerPlayer sendTo) {
			UUID uuid = toSync.getUUID();
			Set<ServerPlayer> trackers = trackedPlayers.get(uuid);
            if (trackers.remove(sendTo) && trackers.isEmpty()) {
                trackedPlayers.remove(uuid);
            }
        }
	}

  	public static void loadGenderInfoAsync(UUID uuid, boolean markForSync) {
  		Thread thread = new Thread(() -> WildfireGender.loadGenderInfo(uuid, markForSync));
		thread.setName("WFGM_GetPlayer-" + uuid);
		thread.setDaemon(true);
  		thread.start();
  	}

	public static GenderPlayer loadGenderInfo(UUID uuid, boolean markForSync) {
		return GenderPlayer.loadCachedPlayer(uuid, markForSync);
	}
}