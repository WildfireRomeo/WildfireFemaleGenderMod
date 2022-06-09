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
import com.wildfire.main.networking.PacketSendGenderInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;


public class WildfireGenderServer implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(WildfireGender.MODID, "send_gender_info"),
        (server, playerEntity, handler, buf, responseSender) -> {
            PacketSendGenderInfo.handle(server, playerEntity, handler, buf, responseSender);
        });
    }
}