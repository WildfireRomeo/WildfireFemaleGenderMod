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

package com.wildfire.main;

import com.wildfire.main.config.Configuration;

public class Breasts {

    public float xOffset = Configuration.BREASTS_OFFSET_X.getDefault(), yOffset = Configuration.BREASTS_OFFSET_Y.getDefault(), zOffset = Configuration.BREASTS_OFFSET_Z.getDefault();
    public float cleavage = Configuration.BREASTS_CLEAVAGE.getDefault();
    public boolean isUniboob = Configuration.BREASTS_UNIBOOB.getDefault();
}
