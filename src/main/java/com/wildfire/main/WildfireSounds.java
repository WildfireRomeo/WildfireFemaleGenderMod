package com.wildfire.main;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class WildfireSounds {
	public static Identifier SND1 = new Identifier("wildfire_gender", "female_hurt1");
	public static SoundEvent FEMALE_HURT1 = new SoundEvent(SND1);
	
	public static Identifier SND2 = new Identifier("wildfire_gender", "female_hurt2");
	public static SoundEvent FEMALE_HURT2 = new SoundEvent(SND2);
	
	/*private static ResourceLocation maleHurt1 = new ResourceLocation("wildfire_gender", "male_hurt1");
	public static SoundEvent MALE_HURT1 = new SoundEvent(maleHurt1);*/
}
