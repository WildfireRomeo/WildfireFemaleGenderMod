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

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.api.IGenderArmor;
import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.main.config.GeneralClientConfig;
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.render.GenderLayer;

import java.util.Set;
import java.util.UUID;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkHooks;
import org.lwjgl.glfw.GLFW;

public class WildfireEventHandler {

	public static final KeyMapping toggleEditGUI = new KeyMapping("key.wildfire_gender.gender_menu", GLFW.GLFW_KEY_G, "category.wildfire_gender.generic");

	@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = WildfireGender.MODID)
	private static class ClientModEventBusListeners {
		@SubscribeEvent
		public static void entityLayers(EntityRenderersEvent.AddLayers event) {
			for (String skinName : event.getSkins()) {
				LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = event.getSkin(skinName);
				if (renderer != null) {
					//TODO - 1.20: Switch to get model manager from event https://github.com/MinecraftForge/MinecraftForge/pull/9562
					renderer.addLayer(new GenderLayer(renderer, Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager()));
				}
			}
		}

		@SubscribeEvent
		public static void setupClient(FMLClientSetupEvent event) {
			MinecraftForge.EVENT_BUS.register(new WildfireEventHandler());
		}

		@SubscribeEvent
		public static void registerKeybindings(RegisterKeyMappingsEvent event) {
			event.register(toggleEditGUI);
		}
	}


	private int toastTick = 0;
	private boolean showedToast = false;
 	@SubscribeEvent
	public void onGUI(TickEvent.ClientTickEvent evt) {
 		if (Minecraft.getInstance().level == null) {
			WildfireGender.CLOTHING_PLAYERS.clear();
			toastTick = 0;
		} else {
			 //toastTick++;
			 if(toastTick > 100 && !showedToast) {
				 Minecraft.getInstance().getToasts().addToast(new Toast() {
					 @Override
					 public Visibility render(GuiGraphics graphics, ToastComponent component, long p_94898_) {
						 RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

						 graphics.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
						 Font font = component.getMinecraft().font;
						 graphics.drawString(font, Component.translatable("category.wildfire_gender.generic"), 0, 7, 0xFF000000, false);
						 graphics.drawString(font, Component.translatable("toast.wildfire_gender.get_started", toggleEditGUI.getTranslatedKeyMessage()), 0, 18, -1, false);

						 return Visibility.SHOW;
					 }
				 });
				 showedToast = true;
			 }
		}
		boolean isVanillaServer = true;
		try {
			ClientPacketListener connection = Minecraft.getInstance().getConnection();
			isVanillaServer = connection != null && NetworkHooks.isVanillaConnection(connection.getConnection());
		} catch(Exception ignored) {}

		if(!isVanillaServer) {
			//20 ticks per second / 5 = 4 times per second

			timer++;
			if (timer >= 5) {
				//System.out.println("sync");
				try {
					Player player = Minecraft.getInstance().player;
					if (player == null) {
						return;
					}
					GenderPlayer aPlr = WildfireGender.getPlayerById(player.getUUID());
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
			IGenderArmor armor = WildfireHelper.getArmorConfig(evt.player.getItemBySlot(EquipmentSlot.CHEST));
			aPlr.getLeftBreastPhysics().update(evt.player, armor);
			aPlr.getRightBreastPhysics().update(evt.player, armor);
		}
 	}
 	@SubscribeEvent
	public void onKeyInput(InputEvent.Key evt) {
		if (toggleEditGUI.isDown()) {
			if(WildfireGender.modEnabled) {
				WildfireGender.refreshAllGenders();
				Minecraft.getInstance().setScreen(new WildfirePlayerListScreen());

			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoin(EntityJoinLevelEvent evt) {
		if (evt.getLevel().isClientSide && evt.getEntity() instanceof AbstractClientPlayer plr) {
			UUID uuid = plr.getUUID();
			GenderPlayer aPlr = WildfireGender.getPlayerById(uuid);
			if (aPlr == null) {
				aPlr = new GenderPlayer(uuid);
				WildfireGender.CLOTHING_PLAYERS.put(uuid, aPlr);
				//Mark the player as needing sync if it is the client's own player
				Player player = Minecraft.getInstance().player;
				WildfireGender.loadGenderInfoAsync(uuid, player != null && uuid.equals(player.getUUID()));

				WildfireGender.refreshAllGenders();
			}
		} 
	}

	//TODO: Eventually we may want to replace this with a map or something and replace things like drowning sounds with other drowning sounds
	private final Set<SoundEvent> playerHurtSounds = Set.of(
		SoundEvents.PLAYER_HURT,
		SoundEvents.PLAYER_HURT_DROWN,
		SoundEvents.PLAYER_HURT_FREEZE,
		SoundEvents.PLAYER_HURT_ON_FIRE,
		SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH
	);

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlaySound(PlayLevelSoundEvent.AtEntity event) {
		if (GeneralClientConfig.INSTANCE.disableSoundReplacement.get()) {
			return;
		}
		Holder<SoundEvent> soundHolder = event.getSound();
		if (soundHolder != null) {
			SoundEvent soundEvent = soundHolder.get();
			if (playerHurtSounds.contains(soundEvent) && event.getEntity() instanceof Player p && p.level().isClientSide) {
				//Cancel as we handle all hurt sounds manually so that we can
				event.setCanceled(true);
				if (p.hurtTime == p.hurtDuration && p.hurtTime > 0) {
					//Note: We check hurtTime == hurtDuration and hurtTime > 0 or otherwise when the server sends a hurt sound to the client
					// and the client will check itself instead of the player who was damaged.
					GenderPlayer plr = WildfireGender.getPlayerById(p.getUUID());
					if (plr != null && plr.hasHurtSounds() && plr.getGender().hasFemaleHurtSounds()) {
						//If the player who produced the hurt sound is a female sound replace it
						soundEvent = Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2;
					}
				} else {
					Player player = Minecraft.getInstance().player;
					if (player != null && p.getUUID().equals(player.getUUID())) {
						//Skip playing remote hurt sounds. Note: sounds played via /playsound will not be intercepted
						// as they are played directly
						//Note: This might behave slightly strangely if a mod is manually firing a player damage sound
						// only on the server and not also on the client
						//TODO: Ideally we would fix that edge case but I find it highly unlikely it will ever actually occur
						return;
					}
				}
				p.level().playLocalSound(p.getX(), p.getY(), p.getZ(), soundEvent, event.getSource(), event.getNewVolume(), event.getNewPitch(), false);
			}
		}
	}
}
