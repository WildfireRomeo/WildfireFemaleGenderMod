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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.ClientModInitializer;

public class WildfireGender implements ClientModInitializer {
	public static final String VERSION = "3.1";
  	public static final String MODID = "wildfire_gender";
	public static Map<UUID, GenderPlayer> CLOTHING_PLAYERS = new HashMap<>();

	@Override
  	public void onInitializeClient() {
		WildfireEventHandler.registerClientEvents();
    }

	@Nullable
	public static GenderPlayer getPlayerById(UUID id) {
		  return CLOTHING_PLAYERS.get(id);
	}

	public static GenderPlayer getOrAddPlayerById(UUID id) {
		return CLOTHING_PLAYERS.computeIfAbsent(id, GenderPlayer::new);
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