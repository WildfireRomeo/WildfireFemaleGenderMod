/*
    Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
    Copyright (C) 2023 WildfireRomeo

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.main.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;

public class FloatConfigKey extends NumberConfigKey<Float> {

    public FloatConfigKey(String key, Float defaultValue) {
        super(key, defaultValue);
    }

    public FloatConfigKey(String key, float defaultValue, float minInclusive, float maxInclusive) {
        super(key, defaultValue, minInclusive, maxInclusive);
    }

    @Override
    protected Float read(JsonElement element) {
        // note that we clamp float values instead of allowing them to be reset to their default to
        // be a bit more user-friendly if the min/max value for this key is modified, and the player's
        // previous config value would now be outside the allowed range for this key.
        return MathHelper.clamp(super.read(element), getMinInclusive(), getMaxInclusive());
    }

    @Override
    protected Float fromPrimitive(JsonPrimitive primitive) {
        return primitive.getAsFloat();
    }

    public float getMinInclusive() {
        //Note: Float.MIN_VALUE is smallest possible positive float
        return minInclusive == null ? -Float.MAX_VALUE : minInclusive;
    }

    public float getMaxInclusive() {
        return maxInclusive == null ? Float.MAX_VALUE : maxInclusive;
    }
}