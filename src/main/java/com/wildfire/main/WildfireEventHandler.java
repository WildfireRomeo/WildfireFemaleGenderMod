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

import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.config.GeneralClientConfig;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.ServerboundSyncPacket;
import com.wildfire.render.GenderLayer;

import java.util.Set;
import java.util.UUID;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class WildfireEventHandler {

	public static final KeyMapping toggleEditGUI = new KeyMapping("key.wildfire_gender.gender_menu", GLFW.GLFW_KEY_G, "category.wildfire_gender.generic") {

		@Override
		public void setDown(boolean value) {
			if (value && !isDown()) {
				//When the key goes from not down to down try to open the wardrobe screen
				Minecraft minecraft = Minecraft.getInstance();
				if (minecraft.screen == null && minecraft.player != null) {
					minecraft.setScreen(new WardrobeBrowserScreen(minecraft.player.getUUID()));
				}
			}
			super.setDown(value);
		}
	};

	@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = WildfireGender.MODID)
	private static class ClientModEventBusListeners {
		@SubscribeEvent
		public static void entityLayers(EntityRenderersEvent.AddLayers event) {
			for (PlayerSkin.Model model : event.getSkins()) {
				if (event.getSkin(model) instanceof PlayerRenderer renderer) {
					renderer.addLayer(new GenderLayer<>(renderer, event.getContext().getModelManager()));
				}
			}
			if (event.getRenderer(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer renderer) {
				renderer.addLayer(new GenderLayer<>(renderer, event.getContext().getModelManager()));
			}
		}

		@SubscribeEvent
		public static void setupClient(FMLClientSetupEvent event) {
			NeoForge.EVENT_BUS.register(new WildfireEventHandler());
		}

		@SubscribeEvent
		public static void registerKeybindings(RegisterKeyMappingsEvent event) {
			event.register(toggleEditGUI);
		}
	}

	private int timer = 0;
	private int toastTick = 0;
	private boolean showedToast = false;
 	@SubscribeEvent
	public void onGUI(ClientTickEvent.Post evt) {
		Player player = Minecraft.getInstance().player;
 		if (Minecraft.getInstance().level == null || player == null) {
			toastTick = 0;
			return;
		}/* else if (!showedToast && toastTick++ > 100) {
			 Minecraft.getInstance().getToasts().addToast(new Toast() {
				 @Nonnull
				 @Override
				 public Visibility render(@Nonnull GuiGraphics graphics, @Nonnull ToastComponent component, long timeSinceLastVisible) {
					 RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

					 graphics.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
					 Font font = component.getMinecraft().font;
					 graphics.drawString(font, Component.translatable("category.wildfire_gender.generic"), 0, 7, 0xFF000000, false);
					 graphics.drawString(font, Component.translatable("toast.wildfire_gender.get_started", toggleEditGUI.getTranslatedKeyMessage()), 0, 18, -1, false);

					 return Visibility.SHOW;
				 }
			 });
			 showedToast = true;
		 }*/
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection != null) {
			//20 ticks per second / 5 = 4 times per second
			if (connection.hasChannel(ServerboundSyncPacket.TYPE) && timer++ % 5 == 0) {
				PlayerConfig plr = WildfireGender.getPlayerById(player.getUUID());
				if (plr != null && plr.needsSync) {
					PacketDistributor.sendToServer(new ServerboundSyncPacket(plr));
					plr.needsSync = false;
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityTick(EntityTickEvent.Post evt) {
		Entity entity = evt.getEntity();
		if (entity.level().isClientSide && entity instanceof LivingEntity living && (living instanceof Player || living instanceof ArmorStand)) {
			EntityConfig cfg = EntityConfig.getEntity(living);
            if (cfg != null) {
                if (entity instanceof ArmorStand) {
                    cfg.readFromStack(living.getItemBySlot(EquipmentSlot.CHEST));
                }
                cfg.tickBreastPhysics(living);
            }
        }
	}

	@SubscribeEvent
	public void onPlayerJoin(EntityJoinLevelEvent evt) {
		if (evt.getLevel().isClientSide && evt.getEntity() instanceof AbstractClientPlayer plr) {
			UUID uuid = plr.getUUID();
			PlayerConfig aPlr = WildfireGender.getPlayerById(uuid);
			if (aPlr == null) {
				aPlr = new PlayerConfig(uuid);
				WildfireGender.PLAYER_CACHE.put(uuid, aPlr);
				//Mark the player as needing sync if it is the client's own player
				Player player = Minecraft.getInstance().player;
				WildfireGender.loadGenderInfo(uuid, player != null && uuid.equals(player.getUUID()));
			}
		}
	}

	@SubscribeEvent
	public void onEntityLeave(EntityLeaveLevelEvent evt) {
		if (evt.getLevel().isClientSide) {
			// note that we don't attempt to unload players; they're instead only ever unloaded once we leave a world
			EntityConfig.ENTITY_CACHE.remove(evt.getEntity().getUUID());
		}
	}

	@SubscribeEvent
	public void disconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		WildfireGender.PLAYER_CACHE.clear();
		EntityConfig.ENTITY_CACHE.clear();
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
			SoundEvent soundEvent = soundHolder.value();
			if (playerHurtSounds.contains(soundEvent) && event.getEntity() instanceof Player p && p.level().isClientSide) {
				//Cancel as we handle all hurt sounds manually so that we can
				event.setCanceled(true);
				if (p.hurtTime == p.hurtDuration && p.hurtTime > 0) {
					//Note: We check hurtTime == hurtDuration and hurtTime > 0 or otherwise when the server sends a hurt sound to the client
					// and the client will check itself instead of the player who was damaged.
					PlayerConfig plr = WildfireGender.getPlayerById(p.getUUID());
					if (plr != null && plr.hasHurtSounds()) {
						SoundEvent soundOverride = plr.getGender().getHurtSound();
						if (soundOverride != null) {
							//If the player who produced the hurt sound is a female sound replace it
							soundEvent = soundOverride;
						}
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
