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
import com.wildfire.render.GenderArmorLayer;
import com.wildfire.render.GenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public final class WildfireEventHandler {
	private WildfireEventHandler() {
		throw new UnsupportedOperationException();
	}

	private static final KeyBinding CONFIG_KEYBIND;
	private static int timer = 0;

	static {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			// this has to be wrapped in a lambda to ensure that a dedicated server won't crash during startup
			// while executing this static block
			CONFIG_KEYBIND = Util.make(() -> {
				KeyBinding keybind = new KeyBinding("key.wildfire_gender.gender_menu", GLFW.GLFW_KEY_G, "category.wildfire_gender.generic");
				KeyBindingHelper.registerKeyBinding(keybind);
				return keybind;
			});
		} else {
			CONFIG_KEYBIND = null;
		}
	}

	/**
	 * Register all events applicable to the server-side for both a dedicated server and singleplayer
	 */
	public static void registerCommonEvents() {
		EntityTrackingEvents.START_TRACKING.register(WildfireEventHandler::onBeginTracking);
		ServerPlayConnectionEvents.DISCONNECT.register(WildfireEventHandler::playerDisconnected);
	}

	/**
	 * Register all client-side events
	 */
	@Environment(EnvType.CLIENT)
	public static void registerClientEvents() {
		ClientEntityEvents.ENTITY_LOAD.register(WildfireEventHandler::onEntityLoad);
		ClientEntityEvents.ENTITY_UNLOAD.register(WildfireEventHandler::onEntityUnload);
		ClientTickEvents.END_CLIENT_TICK.register(WildfireEventHandler::onClientTick);
		ClientPlayConnectionEvents.DISCONNECT.register(WildfireEventHandler::clientDisconnect);
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(WildfireEventHandler::registerRenderLayers);
	}

	/**
	 * Attach breast render layers to players and armor stands
	 */
	@Environment(EnvType.CLIENT)
	private static void registerRenderLayers(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer,
	                                         LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper,
	                                         EntityRendererFactory.Context context) {
		if(entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
			registrationHelper.register(new GenderLayer<>(playerRenderer));
			registrationHelper.register(new GenderArmorLayer<>(playerRenderer, context.getModelManager(), context.getEquipmentModelLoader()));
		} else if(entityRenderer instanceof ArmorStandEntityRenderer armorStandRenderer) {
			registrationHelper.register(new GenderArmorLayer<>(armorStandRenderer, context.getModelManager(), context.getEquipmentModelLoader()));
		}
	}

	/**
	 * Load data for a loaded player if applicable
	 */
	@Environment(EnvType.CLIENT)
	private static void onEntityLoad(Entity entity, World world) {
		if(!world.isClient() || MinecraftClient.getInstance().player == null) return;
		if(entity instanceof AbstractClientPlayerEntity plr) {
			UUID uuid = plr.getUuid();
			boolean isClientPlayer = uuid.equals(MinecraftClient.getInstance().player.getUuid());
			WildfireGenderClient.loadPlayerIfMissing(uuid, isClientPlayer);
		}
	}

	/**
	 * Remove (non-player) entities from the client cache when they're unloaded
	 */
	@Environment(EnvType.CLIENT)
	private static void onEntityUnload(Entity entity, World world) {
		// note that we don't attempt to unload players; they're instead only ever unloaded once we leave a world,
		// or once they disconnect
		EntityConfig.ENTITY_CACHE.remove(entity.getUuid());
	}

	/**
	 * Perform various actions that should happen once per client tick, such as syncing client player settings
	 * to the server.
	 */
	@Environment(EnvType.CLIENT)
	private static void onClientTick(MinecraftClient client) {
		if(client.world == null || client.player == null) return;

		// Only attempt to sync if the server will accept the packet, and only once every 5 ticks, or around 4 times a second
		if(ServerboundSyncPacket.canSend() && timer++ % 5 == 0) {
			PlayerConfig aPlr = WildfireGender.getPlayerById(client.player.getUuid());
			// sendToServer will only actually send a packet if any changes have been made that need to be synced,
			// or if we haven't synced before.
			if(aPlr != null) WildfireSync.sendToServer(aPlr);
		}

		if(CONFIG_KEYBIND.wasPressed() && client.currentScreen == null) {
			client.setScreen(new WardrobeBrowserScreen(null, client.player.getUuid()));
		}
	}

	/**
	 * Clears all caches when the client player disconnects from a server/closes a singleplayer world
	 */
	@Environment(EnvType.CLIENT)
	private static void clientDisconnect(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
		WildfireGender.PLAYER_CACHE.clear();
		EntityConfig.ENTITY_CACHE.clear();
	}

	/**
	 * Removes a disconnecting player from the cache on a server
	 */
	private static void playerDisconnected(ServerPlayNetworkHandler handler, MinecraftServer server) {
		WildfireGender.PLAYER_CACHE.remove(handler.getPlayer().getUuid());
	}

	/**
	 * Send a sync packet when a player enters the render distance of another player
	 */
	private static void onBeginTracking(Entity tracked, ServerPlayerEntity syncTo) {
		if(tracked instanceof PlayerEntity toSync) {
			PlayerConfig genderToSync = WildfireGender.getPlayerById(toSync.getUuid());
			if(genderToSync == null) return;
			// Note that we intentionally don't check if we've previously synced a player with this code path;
			// because we use entity tracking to sync, it's entirely possible that one player would leave the
			// tracking distance of another, change their settings, and then re-enter their tracking distance;
			// we wouldn't sync while they're out of tracking distance, and as such, their settings would be out
			// of sync until they relog.
			WildfireSync.sendToClient(syncTo, genderToSync);
		}
	}
}
