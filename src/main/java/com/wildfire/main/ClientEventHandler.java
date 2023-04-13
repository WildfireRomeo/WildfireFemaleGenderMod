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

import java.util.Set;
import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ClientEventHandler {
	public static final KeyBinding GUI_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.wildfire_gender.gender_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wildfire_gender.generic"));
	private static final Identifier SEND_SYNC_IDENTIFIER = WildfireGender.id("send_gender_info");
	private static int timer = 0;

	public static void registerClientEvents() {
		ClientEntityEvents.ENTITY_LOAD.register(ClientEventHandler::onEntityLoad);
		ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::onTick);
		ClientPlayNetworking.registerGlobalReceiver(WildfireGender.id("hurt"), ClientEventHandler::hurtPacket);
		ClientPlayNetworking.registerGlobalReceiver(WildfireGender.id("sync"), PacketSync::handle);
	}

	private static void onTick(MinecraftClient client) {
		if(client.world == null) {
			timer = 0;
			WildfireGender.CLOTHING_PLAYERS.clear();
			return;
		}
		// the client player realistically shouldn't be null if the world isn't null, but still check anyway
		// to keep IntelliJ happy
		if(client.player == null) return;

		if(ClientPlayNetworking.canSend(SEND_SYNC_IDENTIFIER)) {
			// attempt to sync 4 times a second
			if(++timer % 5 == 0) {
				try {
					GenderPlayer aPlr = WildfireGender.getPlayerById(client.player.getUuid());
					if(aPlr == null) return;
					PacketSendGenderInfo.sendToServer(aPlr);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}

		while(GUI_KEYBIND.wasPressed() && client.currentScreen == null) {
			client.setScreen(new WildfirePlayerListScreen(client));
		}
	}

	private static void onEntityLoad(Entity entity, ClientWorld world) {
		if(MinecraftClient.getInstance().player == null) return;

		if(entity instanceof AbstractClientPlayerEntity plr) {
			UUID uuid = plr.getUuid();
			GenderPlayer aPlr = WildfireGender.getPlayerById(plr.getUuid());
			if(aPlr != null) return;

			aPlr = new GenderPlayer(uuid);
			WildfireGender.CLOTHING_PLAYERS.put(uuid, aPlr);
			WildfireGender.loadGenderInfoAsync(uuid, uuid.equals(MinecraftClient.getInstance().player.getUuid()));
		}
	}

	private static void hurtPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
		if(client.world == null) {
			WildfireGender.LOGGER.warn("Received hurt packet while the world was unset; discarding");
			return;
		}

		PlayerEntity player = client.world.getPlayerByUuid(buf.readUuid());
		if(player == null) return;
		GenderPlayer.Gender gender = buf.readEnumConstant(GenderPlayer.Gender.class);
		if(!buf.readBoolean()) return;

		final SoundEvent hurtSound = (gender.hasFemaleHurtSounds()
				? (Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2)
				: null);
		if(hurtSound == null) return;

		client.execute(() -> {
			long randomLong = player.getRandom().nextLong();
			float pitch = (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F;
			client.getSoundManager().play(new EntityTrackingSoundInstance(hurtSound, SoundCategory.PLAYERS, 1f, pitch, player, randomLong));
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
