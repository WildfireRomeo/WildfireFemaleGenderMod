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

import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.render.GenderLayer;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkHooks;
import org.lwjgl.glfw.GLFW;

public class WildfireEventHandler {

	public static final KeyMapping toggleEditGUI = new KeyMapping("wildfire_gender.key.gui", GLFW.GLFW_KEY_G, "key.categories.wildfire_gender");

	@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = WildfireGender.MODID)
	private static class ClientModEventBusListeners {
		@SubscribeEvent
		public static void entityLayers(EntityRenderersEvent.AddLayers event) {
			for (String skinName : event.getSkins()) {
				LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = event.getSkin(skinName);
				if (renderer != null) {
					renderer.addLayer(new GenderLayer(renderer));
				}
			}
		}

		@SubscribeEvent
		public static void setupClient(FMLClientSetupEvent event) {
			ClientRegistry.registerKeyBinding(toggleEditGUI);
			MinecraftForge.EVENT_BUS.register(new WildfireEventHandler());
		}
	}

 	@SubscribeEvent
	public void onGUI(TickEvent.ClientTickEvent evt) {
 		if (Minecraft.getInstance().level == null) {
			WildfireGender.CLOTHING_PLAYERS.clear();
		}

		boolean isVanillaServer = true;
		try {
			isVanillaServer = NetworkHooks.isVanillaConnection(Minecraft.getInstance().getConnection().getConnection());
		} catch(Exception ignored) {}

		if(!isVanillaServer) {
			//20 ticks per second / 5 = 4 times per second

			timer++;
			if (timer >= 5) {
				//System.out.println("sync");
				try {
					GenderPlayer aPlr = WildfireGender.getPlayerById(Minecraft.getInstance().player.getUUID());
					//Only sync it if it has changed
					if (aPlr == null || !aPlr.needsSync) {
						return;
					}
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
			GenderPlayer aPlr = WildfireGender.getPlayerById(evt.player.getUUID());
			if(aPlr == null) return;
			aPlr.getLeftBreastPhysics().update(evt.player);
			aPlr.getRightBreastPhysics().update(evt.player);
		}
 	}
 	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		if (toggleEditGUI.isDown()) {

			//String playerUUID = Minecraft.getInstance().player.getGameProfile().getId().toString();
			//if(KittGender.modEnabled) Minecraft.getInstance().displayGuiScreen(new WardrobeBrowserScreen(playerUUID));

			if(WildfireGender.modEnabled) {
				WildfireGender.refreshAllGenders();
				Minecraft.getInstance().setScreen(new WildfirePlayerListScreen(Minecraft.getInstance()));

			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoin(EntityJoinWorldEvent evt) {
		if (evt.getWorld().isClientSide && evt.getEntity() instanceof AbstractClientPlayer plr) {
			UUID uuid = plr.getUUID();
			GenderPlayer aPlr = WildfireGender.getPlayerById(uuid);
			if (aPlr == null) {
				aPlr = new GenderPlayer(uuid);
				WildfireGender.CLOTHING_PLAYERS.put(uuid, aPlr);
				//Mark the player as needing sync if it is the client's own player
				WildfireGender.loadGenderInfoAsync(uuid, uuid.equals(Minecraft.getInstance().player.getUUID()));

				WildfireGender.refreshAllGenders();
			}
		} 
	}

	//TODO: Eventually we may want to replace this with a map or something and replace things like drowning sounds with other drowning sounds
	private final Set<SoundEvent> playerHurtSounds = Set.of(SoundEvents.PLAYER_HURT,
		SoundEvents.PLAYER_HURT_DROWN,
		SoundEvents.PLAYER_HURT_FREEZE,
		SoundEvents.PLAYER_HURT_ON_FIRE,
		SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH
	);

	@SubscribeEvent
	public void onPlaySound(PlaySoundAtEntityEvent event) {
		if (playerHurtSounds.contains(event.getSound()) && event.getEntity() instanceof Player p && p.level.isClientSide) {
			GenderPlayer plr = WildfireGender.getPlayerById(p.getUUID());
			if (plr != null && plr.hurtSounds && plr.gender.hasFemaleHurtSounds()) {
				event.setSound(Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2);
			}
		}
	}
}