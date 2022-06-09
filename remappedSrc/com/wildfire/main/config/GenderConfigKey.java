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

package com.wildfire.main.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wildfire.main.GenderPlayer.Gender;

public class GenderConfigKey extends ConfigKey<Gender> {

    //Do not modify
    private static final Gender[] GENDERS = Gender.values();

    public GenderConfigKey(String key) {
        super(key, Gender.MALE);
    }

    @Override
    protected Gender read(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                int ordinal = primitive.getAsInt();
                if (ordinal >= 0 && ordinal < GENDERS.length) {
                    return GENDERS[ordinal];
                }
            } else {
                return primitive.getAsBoolean() ? Gender.MALE : Gender.FEMALE;
            }
        }
        return defaultValue;
    }

    @Override
    public void save(JsonObject object, Gender value) {
        object.addProperty(key, value.ordinal());
    }
}