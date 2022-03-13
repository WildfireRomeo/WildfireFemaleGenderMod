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
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.main.networking.PacketHurt;
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.main.networking.PacketSync;
import com.wildfire.main.proxy.GenderClient;
import com.wildfire.main.proxy.GenderServer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("wildfire_gender")
public class WildfireGender {
	public static final String VERSION = "2.8";
  	public static final String MODID = "wildfire_gender";

  	public static boolean modEnabled = true;
  	public static final boolean SYNCING_ENABLED = false;

	private static final String PROTOCOL_VERSION = "1";
	//public static SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation("wildfire_gender", "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("wildfire_gender", "main_channel"))
			.clientAcceptedVersions(v -> v == NetworkRegistry.ABSENT || v == NetworkRegistry.ACCEPTVANILLA || v.equals(PROTOCOL_VERSION))
			.serverAcceptedVersions(v -> v == NetworkRegistry.ACCEPTVANILLA || v.equals(PROTOCOL_VERSION))
			.networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

  	public static ArrayList<GenderPlayer> CLOTHING_PLAYER = new ArrayList<GenderPlayer>();
  	public static ArrayList<GenderPlayer> SERVER_PLAYER = new ArrayList<GenderPlayer>();

  	public static final GenderServer PROXY = DistExecutor.safeRunForDist(() -> GenderClient::new, () -> GenderServer::new);

  	public WildfireGender() {
  		File f = new File(System.getProperty("user.dir")  + "/config/KittGender/");
  		if(f.exists()) {
  			boolean legacyConvert = f.renameTo(new File(System.getProperty("user.dir")  + "/config/WildfireGender/"));
		}

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup); //common
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient); //client
    }

	public static GenderPlayer getPlayerByName(String username) {
		for (int i = 0; i < CLOTHING_PLAYER.size(); i++) {
			try {
				if (username.toLowerCase().equals(CLOTHING_PLAYER.get(i).username.toLowerCase())) {
					return CLOTHING_PLAYER.get(i);
				}
			} catch (Exception e) {
				GenderPlayer plr = new GenderPlayer(username);
				CLOTHING_PLAYER.add(plr);
				return plr;
			}
		}
		return null;
	}

	public void setup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new WildfireCommonEvents());

		NETWORK.registerMessage(1, PacketSync.class, PacketSync::encode, PacketSync::new, PacketSync::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		NETWORK.registerMessage(2, PacketSendGenderInfo.class, PacketSendGenderInfo::encode, PacketSendGenderInfo::new, PacketSendGenderInfo::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		//NETWORK.registerMessage(3, PacketSendCape.class, PacketSendCape::encode, PacketSendCape::new, PacketSendCape::handle);
		NETWORK.registerMessage(3, PacketHurt.class, PacketHurt::encode, PacketHurt::new, PacketHurt::handle);
	}
  	public void setupClient(FMLClientSetupEvent event) {
  		PROXY.register();
  	}
  	
  	public static void loadGenderInfoAsync(String uuid) {
  		Thread thread = new Thread(new Runnable() {
  			public void run() {
  				WildfireGender.loadGenderInfo(uuid);
  			}
  		});
		thread.setName("WFGM_GetPlayer-" + uuid);
  		thread.start();
  	}

  	public static void refreshAllGenders() {
		if(Minecraft.getInstance().level == null) return;
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

	public static GenderPlayer loadGenderInfo(String uuid) {
		GenderPlayer plr = GenderPlayer.loadCachedPlayer(uuid);
		return plr;
	}
  
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
	}

	public interface WildfireCB {
		public void onExecute(boolean success, Object data);
	}
}