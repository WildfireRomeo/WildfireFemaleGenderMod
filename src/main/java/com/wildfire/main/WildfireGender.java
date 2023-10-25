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
import com.wildfire.main.networking.WildfireSync;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModLoadingContext;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(WildfireGender.MODID)
public class WildfireGender {

	public static final String VERSION = "3.0.1";
  	public static final String MODID = "wildfire_gender";
	public static final Logger logger = LogUtils.getLogger();

	public static Map<UUID, GenderPlayer> CLOTHING_PLAYERS = new HashMap<>();

  	public WildfireGender() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(WildfireSync::setup); //common
		MinecraftForge.EVENT_BUS.addListener(this::onStartTracking);
		if (FMLEnvironment.dist.isClient()) {
			GeneralClientConfig.register(ModLoadingContext.get().getActiveContainer());
		}
    }

	public static ResourceLocation rl(String path) {
		  return new ResourceLocation(MODID, path);
	}

	@Nullable
	public static GenderPlayer getPlayerById(UUID id) {
		  return CLOTHING_PLAYERS.get(id);
	}

	public static GenderPlayer getOrAddPlayerById(UUID id) {
		return CLOTHING_PLAYERS.computeIfAbsent(id, GenderPlayer::new);
	}

	private void onStartTracking(PlayerEvent.StartTracking evt) {
		if (evt.getTarget() instanceof Player toSync && evt.getEntity() instanceof ServerPlayer sendTo) {
			GenderPlayer genderToSync = WildfireGender.getPlayerById(toSync.getUUID());
			if(genderToSync == null) return;
			// Note that we intentionally don't check if we've previously synced a player with this code path;
			// because we use entity tracking to sync, it's entirely possible that one player would leave the
			// tracking distance of another, change their settings, and then re-enter their tracking distance;
			// we wouldn't sync while they're out of tracking distance, and as such, their settings would be out
			// of sync until they relog.
			WildfireSync.sendToClient(sendTo, genderToSync);
		}
	}
  	
  	public static void loadGenderInfoAsync(UUID uuid, boolean markForSync) {
  		Thread thread = new Thread(() -> WildfireGender.loadGenderInfo(uuid, markForSync));
		thread.setName("WFGM_GetPlayer-" + uuid);
  		thread.start();
  	}

	public static GenderPlayer loadGenderInfo(UUID uuid, boolean markForSync) {
		return GenderPlayer.loadCachedPlayer(uuid, markForSync);
	}
}