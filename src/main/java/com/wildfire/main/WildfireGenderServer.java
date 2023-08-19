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

import com.wildfire.main.networking.WildfireSync;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class WildfireGenderServer implements ModInitializer {
    public void onInitialize() {
        // while this class is named 'Server', this is actually a common code path,
        // so we can safely register here for both sides.
        WildfireSounds.register();
        ServerPlayNetworking.registerGlobalReceiver(WildfireSync.SEND_GENDER_IDENTIFIER, WildfireSync::handle);
        EntityTrackingEvents.START_TRACKING.register(this::onBeginTracking);
    }

    private void onBeginTracking(Entity tracked, ServerPlayerEntity syncTo) {
        if(tracked instanceof PlayerEntity toSync) {
            GenderPlayer genderToSync = WildfireGender.getPlayerById(toSync.getUuid());
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
