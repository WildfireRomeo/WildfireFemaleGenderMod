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

package com.wildfire.api;

//TODO: Docs
public interface IGenderArmor {

    //false for things like elytra
    default boolean coversBreasts() {
        return true;
    }

    //Override and return true for armor that should always hide breasts
    // even if the gender player has breasts show in armor
    // useful for things that would cause clipping due to custom rendering of the armor
    default boolean alwaysHidesBreasts() {
        return false;
    }

    //Basically defines what the resistance is of movement if physics is enabled and enabled for armor
    default float physicsResistance() {
        return 0;
    }

    //TODO: Something for tightness? (basically reduces size of breasts if very tight?)
}