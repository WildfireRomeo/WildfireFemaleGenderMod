package com.wildfire.main.proxy;

import com.wildfire.main.WildfireCommonEvents;
import com.wildfire.main.WildfireEventHandler;
import com.wildfire.main.WildfireHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.ClientRegistry;
import org.jline.keymap.KeyMap;
import org.lwjgl.glfw.GLFW;

import com.wildfire.render.GenderLayer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import javax.swing.text.Keymap;
import java.util.List;
import java.util.ListIterator;

public class GenderClient extends GenderServer {

	public static final KeyMapping toggleEditGUI = new KeyMapping("wildfire_gender.key.gui", GLFW.GLFW_KEY_G, "key.categories.wildfire_gender");

	public void playSound(SoundEvent evt, SoundSource cat, float vol, float pitch, Player ent){
		Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(evt, cat, vol, pitch, ent));
	}
	public void register() {
		ClientRegistry.registerKeyBinding(toggleEditGUI);
  		MinecraftForge.EVENT_BUS.register(new WildfireEventHandler());
	}
}
