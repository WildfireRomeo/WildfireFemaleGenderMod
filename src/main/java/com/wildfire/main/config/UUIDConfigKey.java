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
import java.util.UUID;

public class UUIDConfigKey extends ConfigKey<UUID> {

    public UUIDConfigKey(String key, UUID defaultValue) {
        super(key, defaultValue);
    }

    @Override
    protected UUID read(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                try {
                    return UUID.fromString(primitive.getAsString());
                } catch (Exception ignored) {
                    //If we can't parse it then fallback to the default
                }
            }
        }
        return defaultValue;
    }

    @Override
    public void save(JsonObject object, UUID value) {
        object.addProperty(key, value.toString());
    }
}