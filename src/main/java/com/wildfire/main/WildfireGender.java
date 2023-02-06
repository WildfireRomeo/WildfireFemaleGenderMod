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

import com.wildfire.api.IGenderArmor;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.main.networking.PacketSync;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(WildfireGender.MODID)
public class WildfireGender {
	public static final String VERSION = "2.8";
  	public static final String MODID = "wildfire_gender";

  	public static boolean modEnabled = true;
  	public static final boolean SYNCING_ENABLED = false;

	private static final String PROTOCOL_VERSION = "2";
	//public static SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(WildfireGender.MODID, "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(WildfireGender.MODID, "main_channel"))
			.clientAcceptedVersions(v -> v.equals(NetworkRegistry.ABSENT) || v.equals(NetworkRegistry.ACCEPTVANILLA) || v.equals(PROTOCOL_VERSION))
			.serverAcceptedVersions(v -> v.equals(NetworkRegistry.ACCEPTVANILLA) || v.equals(PROTOCOL_VERSION))
			.networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

	public static Map<UUID, GenderPlayer> CLOTHING_PLAYERS = new HashMap<>();

  	public WildfireGender() {
		Path configDir = FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get());
  		File legacyFolder = configDir.resolve("KittGender").toFile();
  		if (legacyFolder.exists()) {
  			boolean legacyConvert = legacyFolder.renameTo(configDir.resolve("WildfireGender").toFile());
		}

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup); //common
		modEventBus.addListener(this::registerCapabilities);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoginEvent);
    }

	@Nullable
	public static GenderPlayer getPlayerById(UUID id) {
		  return CLOTHING_PLAYERS.get(id);
	}

	public static GenderPlayer getOrAddPlayerById(UUID id) {
		return CLOTHING_PLAYERS.computeIfAbsent(id, GenderPlayer::new);
	}

	public void setup(FMLCommonSetupEvent event) {
		NETWORK.registerMessage(1, PacketSync.class, PacketSync::encode, PacketSync::new, PacketSync::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		NETWORK.registerMessage(2, PacketSendGenderInfo.class, PacketSendGenderInfo::encode, PacketSendGenderInfo::new, PacketSendGenderInfo::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		//NETWORK.registerMessage(3, PacketSendCape.class, PacketSendCape::encode, PacketSendCape::new, PacketSendCape::handle);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		  event.register(IGenderArmor.class);
	}

	public void onPlayerLoginEvent(PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (!player.level.isClientSide() && player instanceof ServerPlayer sp) {
			//Send all other players to the player who joined. Note: We don't send the player to
			// other players as that will happen once the player finishes sending themselves to the server
			PacketSync.sendTo(sp);
		}
	}
  	
  	public static void loadGenderInfoAsync(UUID uuid, boolean markForSync) {
  		Thread thread = new Thread(() -> WildfireGender.loadGenderInfo(uuid, markForSync));
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

	public static GenderPlayer loadGenderInfo(UUID uuid, boolean markForSync) {
		return GenderPlayer.loadCachedPlayer(uuid, markForSync);
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
		void onExecute(boolean success, Object data);
	}
}