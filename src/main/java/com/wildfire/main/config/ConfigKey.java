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

public abstract class ConfigKey<TYPE> {

    protected final String key;
    protected final TYPE defaultValue;

    protected ConfigKey(String key, TYPE defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public TYPE getDefault() {
        return defaultValue;
    }

    public final TYPE read(JsonObject obj) {
        JsonElement element = obj.get(key);
        if (element != null) {
            TYPE value = read(element);
            if (validate(value)) {
                //If the value is valid, return it otherwise return the default
                return value;
            }
        }
        return defaultValue;
    }

    protected abstract TYPE read(JsonElement element);

    public abstract void save(JsonObject object, TYPE value);

    public boolean validate(TYPE value) {
        return value != null;
    }
}