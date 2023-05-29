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

import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.main.networking.PacketSync;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerEventHandler {
	public static void registerEvents() {
		EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::entityTrackingStart);
		ServerPlayNetworking.registerGlobalReceiver(WildfireGender.id("send_gender_info"), PacketSendGenderInfo::handle);
	}

	private static void entityTrackingStart(Entity entity, ServerPlayerEntity syncTo) {
		if(entity instanceof PlayerEntity toSync) {
			GenderPlayer genderToSync = WildfireGender.getPlayerById(toSync.getUuid());
			if(genderToSync == null) return;
			// Note that we don't check if the player has previously loaded the other player; this is done
			// intentionally, as we only ever re-sync with players that are currently tracking the player when
			// they change their gender settings, *or* when the player re-enters their render distance, and as
			// a result begins tracking them again.
			PacketSync.syncTo(genderToSync, syncTo);
		}
	}
}
