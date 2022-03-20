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

package com.wildfire.main;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.UUID;
import javax.annotation.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;

public class WildfireGender implements ClientModInitializer {
	public static final String VERSION = "2.9";
  	public static final String MODID = "wildfire_gender";

  	public static boolean modEnabled = true;
  	public static final boolean SYNCING_ENABLED = false;

	private static final String PROTOCOL_VERSION = "2";
	public static Map<UUID, GenderPlayer> CLOTHING_PLAYERS = new HashMap<>();

	@Override
  	public void onInitializeClient() {
		File f = new File(System.getProperty("user.dir")  + "/config/KittGender/");
		if(f.exists()) {
			boolean legacyConvert = f.renameTo(new File(System.getProperty("user.dir")  + "/config/WildfireGender/"));
		}

		WildfireEventHandler.registerClientEvents();
		WildfireSounds.register();
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

  	public static void refreshAllGenders() {
		if(MinecraftClient.getInstance().world == null) return;
		/*
  		Thread thread = new Thread(new Runnable() {
			public void run() {
		  		NetworkPlayerInfo[] playersC = Minecraft.getInstance().getConnection().getPlayerInfoMap().toArray(new NetworkPlayerInfo[Minecraft.getInstance().getConnection().getPlayerInfoMap().size()]);
		        for(int h = 0; h < playersC.length; h++) {
					NetworkPlayerInfo loadedPlayer = playersC[h];
					GenderPlayer plr = WildfireGender.getPlayerByName(loadedPlayer.getGameProfile().getId().toString());
					if(plr != null) {
						plr.refreshCape();
					}
				}
			}

		});
		thread.setName("WFGM_GetAllPlayers");
  		thread.start();*/
  	}

	public static GenderPlayer loadGenderInfo(UUID uuid, boolean markForSync) {
		return GenderPlayer.loadCachedPlayer(uuid, markForSync);
	}
  /*
	public static void drawTextLabel(PoseStack m, String txt, int x, int y) {
		GlStateManager._disableBlend();
		Screen.fill(m, x, y, x + (Minecraft.getInstance()).font.width(txt) + 3, y + 11, 1610612736);
		Minecraft.getInstance().font.draw(m, txt, x + 2, y + 2, 16777215);
	}
	public static void drawRightTextLabel(PoseStack m, String txt, int x, int y) {
		GlStateManager._disableBlend();
		int w = (Minecraft.getInstance()).font.width(txt) + 3;
		Screen.fill(m, x - w, y, x, y + 11, 1610612736);
		Minecraft.getInstance().font.draw(m, txt, x - w + 2, y + 2, 16777215);
	}
	public static void drawCenterTextLabel(PoseStack m, String txt, int x, int y) {
		GlStateManager._disableBlend();
		int w = (Minecraft.getInstance()).font.width(txt) + 3;
		Screen.fill(m, x - w / 2, y, x + w / 2 + 1, y + 11, 1610612736);
		Minecraft.getInstance().font.draw(m, txt, x - w / 2 + 2, y + 2, 16777215);
	}*/

	public interface WildfireCB {
		void onExecute(boolean success, Object data);
	}
}