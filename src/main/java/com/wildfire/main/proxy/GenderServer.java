package com.wildfire.main.proxy;

import com.wildfire.main.WildfireCommonEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public class GenderServer {

	public void playSound(SoundEvent evt, SoundSource cat, float vol, float pitch, Player ent) {}

	public void register() {
		MinecraftForge.EVENT_BUS.register(new WildfireCommonEvents());
	}
}
