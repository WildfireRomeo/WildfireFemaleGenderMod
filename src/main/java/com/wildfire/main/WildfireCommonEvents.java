package com.wildfire.main;

import com.mojang.math.Vector3d;
import com.wildfire.main.networking.PacketHurt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.core.jmx.Server;

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
