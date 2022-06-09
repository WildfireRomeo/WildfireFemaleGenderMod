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

package com.wildfire.main;

import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;
import java.util.function.Consumer;

public class Breasts {

    private float xOffset = Configuration.BREASTS_OFFSET_X.getDefault(), yOffset = Configuration.BREASTS_OFFSET_Y.getDefault(), zOffset = Configuration.BREASTS_OFFSET_Z.getDefault();
    private float cleavage = Configuration.BREASTS_CLEAVAGE.getDefault();
    private boolean uniboob = Configuration.BREASTS_UNIBOOB.getDefault();

    private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
        if (key.validate(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    public float getXOffset() {
        return xOffset;
    }

    public boolean updateXOffset(float value) {
        return updateValue(Configuration.BREASTS_OFFSET_X, value, v -> this.xOffset = v);
    }

    public float getYOffset() {
        return yOffset;
    }

    public boolean updateYOffset(float value) {
        return updateValue(Configuration.BREASTS_OFFSET_Y, value, v -> this.yOffset = v);
    }

    public float getZOffset() {
        return zOffset;
    }

    public boolean updateZOffset(float value) {
        return updateValue(Configuration.BREASTS_OFFSET_Z, value, v -> this.zOffset = v);
    }

    public float getCleavage() {
        return cleavage;
    }

    public boolean updateCleavage(float value) {
        return updateValue(Configuration.BREASTS_CLEAVAGE, value, v -> this.cleavage = v);
    }

    public boolean isUniboob() {
        return uniboob;
    }

    public boolean updateUniboob(boolean value) {
        return updateValue(Configuration.BREASTS_UNIBOOB, value, v -> this.uniboob = v);
    }
}
