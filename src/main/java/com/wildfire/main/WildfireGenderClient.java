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

import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.WildfireSync;
import com.wildfire.resources.GenderArmorResourceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class WildfireGenderClient implements ClientModInitializer {
	private static final Executor LOAD_EXECUTOR = Util.getIoWorkerExecutor().named("wildfire_gender$loadPlayerData");

	@Override
	public void onInitializeClient() {
		WildfireSounds.register();
		WildfireSync.registerClient();
		WildfireEventHandler.registerClientEvents();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GenderArmorResourceManager.INSTANCE);
	}

	public static CompletableFuture<@Nullable PlayerConfig> loadGenderInfo(UUID uuid, boolean markForSync) {
		return CompletableFuture.supplyAsync(() -> PlayerConfig.loadCachedPlayer(uuid, markForSync), LOAD_EXECUTOR);
	}

	public static void loadPlayerIfMissing(UUID uuid, boolean markForSync) {
		if(WildfireGender.PLAYER_CACHE.containsKey(uuid)) return;
		WildfireGender.getOrAddPlayerById(uuid);
		loadGenderInfo(uuid, markForSync);
	}
}
