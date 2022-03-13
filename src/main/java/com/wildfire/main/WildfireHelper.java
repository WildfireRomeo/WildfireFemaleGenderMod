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
import java.util.concurrent.ThreadLocalRandom;

public class WildfireHelper {

    public static final String SYNC_URL = "https://wildfiremod.tk";

    public static class Obfuscation {

        //NetworkPlayerInfo.java
        public static final String NETWORK_PLAYER_INFO = "field_175157_a";
        public static final String PLAYER_TEXTURES = "field_187107_a";
        public static final String LAYER_RENDERERS = "field_177097_h";
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    public static float randFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble((double) min, (double) max + 1);
    }
}
