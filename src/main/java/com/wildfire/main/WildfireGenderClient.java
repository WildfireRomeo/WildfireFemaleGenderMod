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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

@Environment(EnvType.CLIENT)
public class WildfireGenderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WildfireSounds.register();
		WildfireSync.registerClient();
		WildfireEventHandler.registerClientEvents();
	}

	public static Future<Optional<PlayerConfig>> loadGenderInfo(UUID uuid, boolean markForSync) {
		return Util.getIoWorkerExecutor().submit(() -> Optional.ofNullable(PlayerConfig.loadCachedPlayer(uuid, markForSync)));
	}

	public static void loadPlayerIfMissing(UUID uuid, boolean markForSync) {
		if(WildfireGender.PLAYER_CACHE.containsKey(uuid)) return;
		WildfireGender.getOrAddPlayerById(uuid);
		loadGenderInfo(uuid, markForSync);
	}
}
