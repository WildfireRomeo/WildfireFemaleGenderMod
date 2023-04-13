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

import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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
}
