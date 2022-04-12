package com.wildfire.api;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WildfireAPI {

    private static Map<Item, IGenderArmor> GENDER_ARMORS = new HashMap<>();

    /**
     * Add custom attributes to the armor you apply this to.
     *
     * @param  item  the item that you are adding {@link IGenderArmor} to.
     * @param  genderArmor the class implementing {@link IGenderArmor} to apply to the item.
     * @see    IGenderArmor
     */
    public static void addGenderArmor(Item item, IGenderArmor genderArmor) {
        GENDER_ARMORS.put(item, genderArmor);
    }

    /**
     * Add custom attributes to the armor you apply this to.
     *
     * @param  uuid  the uuid of the {@link net.minecraft.entity.player.PlayerEntity }.
     * @see    IGenderArmor
     */
    public static GenderPlayer getPlayerById(UUID uuid) {
        return WildfireGender.getPlayerById(uuid);
    }

    /**
     * Get the player's {@link com.wildfire.main.GenderPlayer.Gender }.
     *
     * @param  uuid  the uuid of the {@link PlayerEntity }.
     * @see    GenderPlayer.Gender
     */
    public static GenderPlayer.Gender getPlayerGender(UUID uuid) {
        return WildfireGender.getPlayerById(uuid).getGender();
    }

    /**
     * Load the cached Gender Settings file for the specified {@link UUID }
     *
     * @param  uuid  the uuid of the {@link PlayerEntity }.
     * @param  markForSync true if you want to send the gender settings to the server upon loading.
     */
    public static void loadGenderInfo(UUID uuid, boolean markForSync) {
        WildfireGender.loadGenderInfoAsync(uuid, markForSync);
    }

    /**
     * Get the list of armors supported by Wildfire's Female Gender Mod.
     */
    public static Map<Item, IGenderArmor> getGenderArmors() {
        return GENDER_ARMORS;
    }

}
