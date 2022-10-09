/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022 WildfireRomeo

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
import com.wildfire.main.networking.PacketSync;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class WildfireEventHandler {

	public static final KeyBinding toggleEditGUI = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wildfire_gender.gender_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wildfire_gender.generic"));

	private static int timer = 0;

	public static void registerClientEvents() {

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (!handler.getPlayer().world.isClient()) {
				//Send all other players to the player who joined. Note: We don't send the player to
				// other players as that will happen once the player finishes sending themselves to the server
				PacketSync.sendTo(handler.getPlayer());
			}
		});

		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if(!world.isClient) return;
			if(entity instanceof  AbstractClientPlayerEntity plr) {
				UUID uuid = plr.getUuid();
				GenderPlayer aPlr = WildfireGender.getPlayerById(plr.getUuid());
				if (aPlr == null) {
					aPlr = new GenderPlayer(uuid);
					WildfireGender.CLOTHING_PLAYERS.put(uuid, aPlr);
					WildfireGender.loadGenderInfoAsync(uuid, uuid.equals(MinecraftClient.getInstance().player.getUuid()));
					return;
				}
			}
			/*if(!world.isClient) return;

			if(entity instanceof AbstractClientPlayerEntity plr) {
				UUID uuid = plr.getUuid();
				GenderPlayer aPlr = WildfireGender.getPlayerById(plr.getUuid());
				if(aPlr == null) {
					aPlr = new GenderPlayer(uuid);
					WildfireGender.CLOTHING_PLAYERS.put(uuid, aPlr);
					WildfireGender.loadGenderInfoAsync(uuid, uuid.equals(MinecraftClient.getInstance().player.getUuid()));
					return;
				}
			}*/
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.world == null) WildfireGender.CLOTHING_PLAYERS.clear();

			boolean isVanillaServer = !ClientPlayNetworking.canSend(new Identifier(WildfireGender.MODID, "send_gender_info"));


			if(!isVanillaServer) {
				//20 ticks per second / 5 = 4 times per second

				timer++;
				if (timer >= 5) {
					try {
						GenderPlayer aPlr = WildfireGender.getPlayerById(MinecraftClient.getInstance().player.getUuid());
						if(aPlr == null /*|| !aPlr.needsSync*/) return;
						PacketSendGenderInfo.send(aPlr);
					} catch (Exception e) {
						//e.printStackTrace();
					}
					timer = 0;
				}
			}

			while (toggleEditGUI.wasPressed()) {
				client.setScreen(new WildfirePlayerListScreen(client));
			}
		});

		//Receive hurt

		ClientPlayNetworking.registerGlobalReceiver(new Identifier(WildfireGender.MODID, "hurt"),
		(client, handler, buf, responseSender) -> {
			UUID uuid = buf.readUuid();
			GenderPlayer.Gender gender = buf.readEnumConstant(GenderPlayer.Gender.class);
			boolean hurtSounds = buf.readBoolean();

			//Vector3d pos = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

			SoundEvent hurtSound = null;
			if(gender == GenderPlayer.Gender.FEMALE) {
				hurtSound = Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2;
			}
			if(hurtSound == null) return;

			if(hurtSounds) {
				PlayerEntity ent = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
				if (ent != null) {
					long randomLong = new Random().nextLong(0L,1L);
					final SoundEvent hurtSound2 = hurtSound;
					// ensures it's executed in the main thread
					client.execute(() -> {
						client.getSoundManager().play(new EntityTrackingSoundInstance(hurtSound2, SoundCategory.PLAYERS, 1f, 1f, ent, randomLong));
					});
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(new Identifier(WildfireGender.MODID, "sync"),
		(client, handler, buf, responseSender) -> {
			PacketSync.handle(client, handler, buf, responseSender);
		});
	}

	//TODO: Eventually we may want to replace this with a map or something and replace things like drowning sounds with other drowning sounds
	private final Set<SoundEvent> playerHurtSounds = Set.of(SoundEvents.ENTITY_PLAYER_HURT,
		SoundEvents.ENTITY_PLAYER_HURT_DROWN,
		SoundEvents.ENTITY_PLAYER_HURT_FREEZE,
		SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE,
		SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH
	);
/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlaySound(PlaySoundAtEntityEvent event) {
		if (playerHurtSounds.contains(event.getSound()) && event.getEntity() instanceof Player p && p.level.isClientSide) {
			//Cancel as we handle all hurt sounds manually so that we can
			event.setCanceled(true);
			SoundEvent soundEvent = event.getSound();
			if (p.hurtTime == p.hurtDuration && p.hurtTime > 0) {
				//Note: We check hurtTime == hurtDuration and hurtTime > 0 or otherwise when the server sends a hurt sound to the client
				// and the client will check itself instead of the player who was damaged.
				GenderPlayer plr = WildfireGender.getPlayerById(p.getUUID());
				if (plr != null && plr.hasHurtSounds() && plr.getGender().hasFemaleHurtSounds()) {
					//If the player who produced the hurt sound is a female sound replace it
					soundEvent = Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2;
				}
			} else if (p.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
				//Skip playing remote hurt sounds. Note: sounds played via /playsound will not be intercepted
				// as they are played directly
				//Note: This might behave slightly strangely if a mod is manually firing a player damage sound
				// only on the server and not also on the client
				//TODO: Ideally we would fix that edge case but I find it highly unlikely it will ever actually occur
				return;
			}
			p.level.playLocalSound(p.getX(), p.getY(), p.getZ(), soundEvent, event.getCategory(), event.getVolume(), event.getPitch(), false);
		}
	}*/
}
