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
import com.wildfire.main.networking.PacketHurt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WildfireCommonEvents {

    public WildfireCommonEvents() {

    }
    @SubscribeEvent
    public void onHurt(LivingHurtEvent e) {

        if (e.getEntity() == null || !(e.getEntity() instanceof ServerPlayer))
            return;

        float damageAmount = e.getAmount();
        if(damageAmount > 0.0) {
            ServerPlayer p = (ServerPlayer) e.getEntity();

            Vec3 pos = p.getPosition(0);

            GenderPlayer aPlr = WildfireGender.getPlayerByName(p.getStringUUID());
            if (aPlr == null) return;

            PacketHurt.send(false, pos, aPlr);
        }
    }
}
