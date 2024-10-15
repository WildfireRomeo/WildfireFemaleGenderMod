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

package com.wildfire.api;

import com.wildfire.main.WildfireGenderClient;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.Gender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class WildfireAPI {

    private static final Map<Item, IGenderArmor> GENDER_ARMORS = new HashMap<>();

    /**
     * Add custom physics resistance attributes to a chestplate
     *
     * @param  item  the item that you are linking this {@link IGenderArmor} to
     * @param  genderArmor the class implementing the {@link IGenderArmor} to apply to the item
     * @see    IGenderArmor
     */
    public static void addGenderArmor(Item item, IGenderArmor genderArmor) {
        GENDER_ARMORS.put(item, genderArmor);
    }

    /**
     * Get the config for a {@link PlayerEntity}
     *
     * @param  uuid  the uuid of the target {@link PlayerEntity}
     * @see    PlayerConfig
     */
    public static @Nullable PlayerConfig getPlayerById(UUID uuid) {
        return WildfireGender.getPlayerById(uuid);
    }

    /**
     * Get the player's {@link Gender}
     *
     * @param  uuid  the uuid of the target {@link PlayerEntity}.
     * @see    Gender
     */
    public static @NotNull Gender getPlayerGender(UUID uuid) {
        PlayerConfig cfg = WildfireGender.getPlayerById(uuid);
        if(cfg == null) return Configuration.GENDER.getDefault();
        return cfg.getGender();
    }

    /**
     * <p>Load the cached Gender Settings file for the specified {@link UUID}</p>
     *
     * <p>You should avoid using this unless you need to, as the mod will do this for you when loading a player entity.</p>
     *
     * @param  uuid  the uuid of the target {@link PlayerEntity}
     * @param  markForSync true if you want to send the gender settings to the server upon loading.
     */
    @Environment(EnvType.CLIENT)
    public static CompletableFuture<@Nullable PlayerConfig> loadGenderInfo(UUID uuid, boolean markForSync) {
        return WildfireGenderClient.loadGenderInfo(uuid, markForSync);
    }

    /**
     * Get every registered {@link IGenderArmor custom armor configuration}
     *
     * @implNote This does not provide vanilla armor configurations; see {@link com.wildfire.render.armor.SimpleGenderArmor} for that.
     */
    public static Map<Item, IGenderArmor> getGenderArmors() {
        return GENDER_ARMORS;
    }

}
