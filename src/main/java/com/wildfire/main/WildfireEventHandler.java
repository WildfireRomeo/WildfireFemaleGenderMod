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

import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.main.networking.PacketSync;

import java.util.UUID;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class WildfireEventHandler {

	public static final KeyBinding toggleEditGUI = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wildfire_gender.gender_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wildfire_gender.generic"));

	private static int timer = 0;

	public static void registerClientEvents() {

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (!handler.getPlayer().getWorld().isClient()) {
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

		ClientPlayNetworking.registerGlobalReceiver(new Identifier(WildfireGender.MODID, "sync"),
		(client, handler, buf, responseSender) -> {
			PacketSync.handle(client, handler, buf, responseSender);
		});
	}
}
