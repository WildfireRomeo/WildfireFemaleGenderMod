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
import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.main.proxy.GenderClient;
import com.wildfire.render.GenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import java.util.Map;

public class WildfireEventHandler {
	
	public WildfireEventHandler() {
		
	}

	@Mod.EventBusSubscriber(value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
	private static class EntityRenderEventsTestClientModStuff {
		@SubscribeEvent
		public static void entityLayers(EntityRenderersEvent.AddLayers event) {

			Map<String, EntityRenderer<? extends Player>> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
			//System.out.println("SKIN MAP: " + skinMap.size());
			skinMap.forEach((string, r) -> {
				LivingEntityRenderer renderer = (LivingEntityRenderer) r;
				renderer.addLayer(new GenderLayer(renderer));
			});
		}
	}

 	@SubscribeEvent
	public void onGUI(TickEvent.ClientTickEvent evt) {
 		if(Minecraft.getInstance().level == null && WildfireGender.CLOTHING_PLAYER.size() > 0) {
			WildfireGender.CLOTHING_PLAYER.clear();
		}

		boolean isVanillaServer = true;
		try {
			isVanillaServer = NetworkHooks.isVanillaConnection(Minecraft.getInstance().getConnection().getConnection());
		} catch(Exception e) {}

		if(!isVanillaServer) {
			//20 ticks per second / 5 = 4 times per second

			timer++;
			if (timer >= 5) {
				//System.out.println("sync");
				try {
					GenderPlayer aPlr = WildfireGender.getPlayerByName(Minecraft.getInstance().player.getStringUUID());
					if(aPlr == null) return;
					PacketSendGenderInfo.send(aPlr);
				} catch (Exception e) {
					//e.printStackTrace();
				}
				timer = 0;
			}
		}
	}

    int timer = 0;
 	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent evt) {
		if(evt.phase == TickEvent.Phase.END && evt.side.isClient()) {
			GenderPlayer aPlr = WildfireGender.getPlayerByName(evt.player.getStringUUID());
			if(aPlr == null) return;
			aPlr.getLeftBreastPhysics().update(evt.player);
			aPlr.getRightBreastPhysics().update(evt.player);
		}
 	}
 	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		if(GenderClient.toggleEditGUI.isDown()) {

			String playerUUID = Minecraft.getInstance().player.getGameProfile().getId().toString();
			//if(KittGender.modEnabled) Minecraft.getInstance().displayGuiScreen(new WardrobeBrowserScreen(playerUUID));

			if(WildfireGender.modEnabled) {
				WildfireGender.refreshAllGenders();
				Minecraft.getInstance().setScreen(new WildfirePlayerListScreen(Minecraft.getInstance()));

			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoin(EntityJoinWorldEvent evt) {
		if(!evt.getWorld().isClientSide) return;

		if(evt.getEntity() instanceof AbstractClientPlayer) {
			AbstractClientPlayer plr = (AbstractClientPlayer) evt.getEntity();

			String playerName = plr.getGameProfile().getId().toString();
			GenderPlayer aPlr = WildfireGender.getPlayerByName(plr.getStringUUID());
			if(aPlr == null) {
				aPlr = new GenderPlayer(plr.getStringUUID());
				WildfireGender.CLOTHING_PLAYER.add(aPlr);
				WildfireGender.loadGenderInfoAsync(plr.getStringUUID());

				WildfireGender.refreshAllGenders();

				return;
			}
		} 
	}
	public boolean addedLayer = false;
  
}