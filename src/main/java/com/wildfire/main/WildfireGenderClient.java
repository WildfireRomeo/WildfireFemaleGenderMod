package com.wildfire.main;

import com.wildfire.main.config.GeneralClientConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;

@Mod(WildfireGender.MODID)
public class WildfireGenderClient {

    public WildfireGenderClient(ModContainer modContainer) {
        modContainer.registerConfig(Type.CLIENT, GeneralClientConfig.INSTANCE.configSpec, "WildfireGender/client.toml");
    }
}