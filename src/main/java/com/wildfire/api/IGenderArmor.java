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

/**
 * Expose this as a capability on your chestplates or items that go in the chest slot to configure how it interacts with breast rendering.
 */
public interface IGenderArmor {

    /**
     * Determines whether this {@link IGenderArmor} "covers" the breasts or if it has an open front ({@code false}) like the elytra.
     *
     * @return {@code true} if the breasts are covered.
     *
     * @implNote Defaults to {@code true}.
     */
    default boolean coversBreasts() {
        return true;
    }

    /**
     * Determines if this {@link IGenderArmor} should always hide the wearer's breasts when worn even if they have {@code showBreastsInArmor} set to {@code true}. This is
     * useful for armors that may have custom rendering that is not compatible with how the breasts render and would just lead to clipping.
     *
     * @return {@code true} to hide the breasts regardless of what {@code showBreastsInArmor} is set to.
     *
     * @implNote Defaults to {@code false}.
     */
    default boolean alwaysHidesBreasts() {
        return false;
    }

    /**
     * The percent of physical resistance this {@link IGenderArmor} provides to the wearer's breasts when calculating the corresponding physics.
     *
     * @return Value between {@code 0} (no resistance, full physics) and {@code 1} (total resistance, no physics).
     *
     * @implNote Defaults to {@code 0} (no resistance, full physics).
     */
    default float physicsResistance() {
        return 0;
    }

    /**
     * Value representing how "tight" this {@link IGenderArmor} is. Tightness "compresses" the breasts against the wearer causing the breasts to appear up to {@code 15%}
     * smaller.
     *
     * @return Value between {@code 0} (no tightness, no size reduction) and {@code 1} (full tightness, {@code 15%} size reduction).
     *
     * @implNote Defaults to {@code 0} (no tightness, no size reduction).
     */
    default float tightness() {
        return 0;
    }
}