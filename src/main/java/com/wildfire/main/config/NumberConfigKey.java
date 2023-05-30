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

package com.wildfire.main.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.annotation.Nullable;

public abstract class NumberConfigKey<TYPE extends Number & Comparable<TYPE>> extends ConfigKey<TYPE> {

    @Nullable
    protected final TYPE minInclusive;
    @Nullable
    protected final TYPE maxInclusive;

    protected NumberConfigKey(String key, TYPE defaultValue) {
        this(key, defaultValue, null, null);
    }

    protected NumberConfigKey(String key, TYPE defaultValue, @Nullable TYPE minInclusive, @Nullable TYPE maxInclusive) {
        super(key, defaultValue);
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    protected abstract TYPE fromPrimitive(JsonPrimitive primitive);

    @Override
    protected TYPE read(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber() || primitive.isString()) {
                try {
                    return fromPrimitive(primitive);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return defaultValue;
    }

    @Override
    public void save(JsonObject object, TYPE value) {
        object.addProperty(key, value);
    }

    @Override
    public boolean validate(TYPE value) {
        if (super.validate(value)) {
            return (minInclusive == null || minInclusive.compareTo(value) <= 0) &&
                   (maxInclusive == null || maxInclusive.compareTo(value) >= 0);
        }
        return false;
    }
}