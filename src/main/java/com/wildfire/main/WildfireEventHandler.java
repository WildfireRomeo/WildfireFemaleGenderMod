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
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.ServerboundSyncPacket;
import com.wildfire.main.networking.WildfireSync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class WildfireEventHandler {
	public static final KeyBinding toggleEditGUI = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wildfire_gender.gender_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wildfire_gender.generic"));
	private static long timer = 0;

	public static void registerClientEvents() {
		ClientEntityEvents.ENTITY_LOAD.register(WildfireEventHandler::onEntityLoad);
		ClientEntityEvents.ENTITY_UNLOAD.register(WildfireEventHandler::onEntityUnload);
		ClientTickEvents.END_CLIENT_TICK.register(WildfireEventHandler::onClientTick);
		ClientPlayConnectionEvents.DISCONNECT.register(WildfireEventHandler::disconnect);
		WildfireSync.registerClient();
	}

	private static void onEntityLoad(Entity entity, World world) {
		if(!world.isClient() || MinecraftClient.getInstance().player == null) return;
		if(entity instanceof AbstractClientPlayerEntity plr) {
			UUID uuid = plr.getUuid();
			PlayerConfig aPlr = WildfireGender.getPlayerById(plr.getUuid());
			if(aPlr == null) {
				aPlr = new PlayerConfig(uuid);
				WildfireGender.PLAYER_CACHE.put(uuid, aPlr);
				WildfireGender.loadGenderInfo(uuid, uuid.equals(MinecraftClient.getInstance().player.getUuid()));
			}
		}
	}

	private static void onEntityUnload(Entity entity, World world) {
		// note that we don't attempt to unload players; they're instead only ever unloaded once we leave a world
		EntityConfig.ENTITY_CACHE.remove(entity.getUuid());
	}

	private static void onClientTick(MinecraftClient client) {
		if(client.world == null || client.player == null) return;

		// Only attempt to sync if the server will accept the packet, and only once every 5 ticks, or around 4 times a second
		if(ServerboundSyncPacket.canSend() && timer++ % 5 == 0) {
			PlayerConfig aPlr = WildfireGender.getPlayerById(client.player.getUuid());
			// sendToServer will only actually send a packet if any changes have been made that need to be synced,
			// or if we haven't synced before.
			if(aPlr != null) WildfireSync.sendToServer(aPlr);
		}

		if(toggleEditGUI.wasPressed() && client.currentScreen == null) {
			client.setScreen(new WardrobeBrowserScreen(null, client.player.getUuid()));
		}
	}

	private static void disconnect(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
		WildfireGender.PLAYER_CACHE.clear();
		EntityConfig.ENTITY_CACHE.clear();
	}
}
