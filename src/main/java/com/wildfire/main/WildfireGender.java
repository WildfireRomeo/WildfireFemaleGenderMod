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
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import com.wildfire.gui.screen.WildfirePlayerListScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.event.*;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.fabricmc.fabric.impl.tag.extension.FabricTagHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

public class WildfireGender implements ClientModInitializer {
	public static final String VERSION = "2.8.1";
  	public static final String MODID = "wildfire_gender";
	private static KeyBinding keyBinding;

  	public static final boolean SYNCING_ENABLED = false;

	public static ArrayList<GenderPlayer> CLOTHING_PLAYER = new ArrayList<GenderPlayer>();

	public static int BREAST_CANCER_BANNER = 1;

	private int timer =0;
	@Override
	public void onInitializeClient() {
		/*try {
			String val = WildfireHelper.post("https://raw.githubusercontent.com/WildfireRomeo/WFGM/main/global.json", null);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		Registry.register(Registry.SOUND_EVENT, WildfireSounds.SND1, WildfireSounds.FEMALE_HURT1);
		Registry.register(Registry.SOUND_EVENT, WildfireSounds.SND2, WildfireSounds.FEMALE_HURT2);

		File f = new File(System.getProperty("user.dir")  + "/config/KittGender/");
  		if(f.exists()) {
  			boolean legacyConvert = f.renameTo(new File(System.getProperty("user.dir")  + "/config/WildfireGender/"));
		}
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.wildfire_gender.gender_menu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"category.wildfire_gender.generic"
		));


		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if(!world.isClient) return;

			if(entity instanceof AbstractClientPlayerEntity) {
				AbstractClientPlayerEntity plr = (AbstractClientPlayerEntity) entity;
				GenderPlayer aPlr = WildfireGender.getPlayerByName(plr.getUuidAsString());
				if(aPlr == null) {
					aPlr = new GenderPlayer(plr.getUuidAsString());
					WildfireGender.CLOTHING_PLAYER.add(aPlr);
					WildfireGender.loadGenderInfoAsync(plr.getUuidAsString());
					return;
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.world == null && CLOTHING_PLAYER.size() > 0) CLOTHING_PLAYER.clear();

			boolean isVanillaServer = !ClientPlayNetworking.canSend(new Identifier("wildfire_gender", "send_gender_info"));

			if(!isVanillaServer) {
				//20 ticks per second / 5 = 4 times per second

				timer++;
				if (timer >= 5) {
					//System.out.println("sync");
					try {
						GenderPlayer aPlr = WildfireGender.getPlayerByName(MinecraftClient.getInstance().player.getUuidAsString());
						if(aPlr == null) return;
						PacketByteBuf buf = PacketByteBufs.create();
						buf.writeString(aPlr.username);
						buf.writeInt(aPlr.gender);
						buf.writeFloat(aPlr.getLeftBreastPhysics().getBreastSize(client.getTickDelta()));
						buf.writeBoolean(aPlr.hurtSounds);
						buf.writeBoolean(aPlr.breast_physics);
						buf.writeBoolean(aPlr.breast_physics_armor);
						buf.writeBoolean(aPlr.show_in_armor);

						buf.writeFloat(aPlr.getBreasts().xOffset);
						buf.writeFloat(aPlr.getBreasts().yOffset);
						buf.writeFloat(aPlr.getBreasts().zOffset);
						buf.writeFloat(aPlr.getBreasts().cleavage);
						buf.writeBoolean(aPlr.getBreasts().isUniboob);

						buf.writeFloat(aPlr.bounceMultiplier);
						buf.writeFloat(aPlr.floppyMultiplier);

						ClientPlayNetworking.send(new Identifier("wildfire_gender", "send_gender_info"), buf);
						//PacketSendGenderInfo.send(aPlr);
					} catch (Exception e) {
						//e.printStackTrace();
					}
					timer = 0;
				}
			}

			//Receive hurt

			ClientPlayNetworking.registerGlobalReceiver(new Identifier("wildfire_gender", "hurt"),
			(client2, handler, buf, responseSender) -> {
				String uuid = buf.readString(36);
				int gender = buf.readInt();
				boolean hurtSounds = buf.readBoolean();

				//Vector3d pos = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

				SoundEvent hurtSound = null;
				if(gender == 0) {
					hurtSound = Math.random() > 0.5f ? WildfireSounds.FEMALE_HURT1 : WildfireSounds.FEMALE_HURT2;
				}
				if(hurtSound == null) return;

				if(hurtSounds) {
					PlayerEntity ent = MinecraftClient.getInstance().world.getPlayerByUuid(UUID.fromString(uuid));
					if (ent != null) {
						client.getSoundManager().play(new EntityTrackingSoundInstance(hurtSound, SoundCategory.PLAYERS, 1f, 1f, ent));
					}
				}
			});

			while (keyBinding.wasPressed()) {
				client.setScreen(new WildfirePlayerListScreen(client));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(new Identifier("wildfire_gender", "sync"),
			(client, handler, buf, responseSender) -> {
				String uuid = buf.readString(36);
				int gender = buf.readInt();
				float bust_size = buf.readFloat();
				boolean hurtSounds = buf.readBoolean();
				boolean breastPhysics = buf.readBoolean();
				boolean breastPhysicsArmor = buf.readBoolean();
				boolean showInArmor = buf.readBoolean();

				float xOffset = buf.readFloat();
				float yOffset = buf.readFloat();
				float zOffset = buf.readFloat();
				float cleavage = buf.readFloat();
				boolean dualPhysics = buf.readBoolean();

				float bounceMultiplier = buf.readFloat();
				float floppyMultiplier = buf.readFloat();
				if(!uuid.equals(MinecraftClient.getInstance().player.getUuidAsString())) {
					GenderPlayer plr = WildfireGender.getPlayerByName(uuid);
					if(plr == null) return;
					plr.gender = gender;
					plr.updateBustSize(bust_size);
					plr.hurtSounds = hurtSounds;
					plr.breast_physics = breastPhysics;
					plr.breast_physics_armor = breastPhysicsArmor;
					plr.show_in_armor = showInArmor;

					plr.getBreasts().xOffset = xOffset;
					plr.getBreasts().yOffset = yOffset;
					plr.getBreasts().zOffset = zOffset;
					plr.getBreasts().cleavage = cleavage;
					plr.getBreasts().isUniboob = dualPhysics;

					plr.bounceMultiplier = bounceMultiplier;
					plr.floppyMultiplier = floppyMultiplier;

					plr.syncStatus = GenderPlayer.SyncStatus.SYNCED;
					plr.lockSettings = true;
					//System.out.println("Received player data " + plr.username);
				} else {
					//System.out.println("Ignoring packet, this is yourself.");
				}
			});
    }

	public static GenderPlayer getPlayerByName(String username) {
		for (int i = 0; i < CLOTHING_PLAYER.size(); i++) {
			try {
				if (username.equalsIgnoreCase(CLOTHING_PLAYER.get(i).username)) {
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

  	public static void loadGenderInfoAsync(String uuid) {
  		Thread thread = new Thread(new Runnable() {
  			public void run() {
  				WildfireGender.loadGenderInfo(uuid);
  			}
  		});
		thread.setName("WFGM_GetPlayer-" + uuid);
  		thread.start();
  	}

	public static GenderPlayer loadGenderInfo(String uuid) {
		return GenderPlayer.loadCachedPlayer(uuid);
	}

	public static void drawTextLabel(MatrixStack m, String txt, int x, int y) {
		GlStateManager._disableBlend();
		Screen.fill(m, x, y, x + (MinecraftClient.getInstance()).textRenderer.getWidth(txt) + 3, y + 11, 1610612736);
		MinecraftClient.getInstance().textRenderer.draw(m, txt, x + 2, y + 2, 16777215);
	}

	public interface WildfireCB {
		void onExecute(boolean success, Object data);
	}
}